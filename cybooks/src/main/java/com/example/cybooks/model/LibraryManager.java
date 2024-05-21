package com.example.cybooks.model;
import com.example.cybooks.api.ApiConnector;
import com.example.cybooks.exception.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LibraryManager {
    private final DataBase db;
    private final ApiConnector apiConnector;
    public LibraryManager(DataBase db) {
        this.db = db;
        this.apiConnector = new ApiConnector();
    }

    public void closeDatabase() {
        db.stopServer();    
    }

    public void registerUser(String name, String email, String address) throws InvalidEmailFormatException, EmailAlreadyExistsException {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new InvalidEmailFormatException("Invalid email format: " + email);
        }

        if (isEmailExists(email)) {
            throw new EmailAlreadyExistsException("Email already exists: " + email);
        }

        User user = new User(name, email, address);
        user.register(db);
    }

    public void updateUser(int userID, String name, String email, String address) throws UserNotFoundException, InvalidEmailFormatException, EmailAlreadyExistsException {
        User user = getUserByID(userID);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + userID);
        }

        if (email != null && !email.isEmpty()) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new InvalidEmailFormatException("Invalid email format: " + email);
            }
            if (isEmailExists(email) && !email.equals(user.getEmail())) {
                throw new EmailAlreadyExistsException("Email already exists: " + email);
            }
            user.setEmail(email);
        }

        if (name != null && !name.isEmpty()) {
            user.setName(name);
        }

        if (address != null && !address.isEmpty()) {
            user.setAddress(address);
        }

        user.update(db);
    }

    public void deleteUser(int userID) throws UserNotFoundException {
        User user = getUserByID(userID);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + userID);
        }

        user.delete(db);
    }

    public void addBook(String isbn, int copiesAvailable) {
        Book book = new Book(isbn, copiesAvailable) ;
        book.register(db);
        for (int i = 0; i < copiesAvailable; i++) {
            BookCopies copy = new BookCopies(isbn);
            copy.register(db);
        }
    }

    public void loanBook(int userID, String isbn) throws UserNotFoundException, NoCopyAvailableException {
        User user = getUserByID(userID);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + userID);
        }
        
        if (!isISBNExistsInCopies(isbn)) {
            this.addBook(isbn, 5);
        }

        BookCopies copy = getAvailableCopyByISBN(isbn);
        if (copy == null) {
            throw new NoCopyAvailableException("No copy available for ISBN: " + isbn);
        }
    
        Loan loan = new Loan(userID, copy.getCopyID());
        loan.register(db);
        copy.setLoaned(true);
        copy.update(db);

        // Decrease number of copiesAvailable :
        ResultSet rs = db.executeQuery("SELECT copiesAvailable FROM Books WHERE isbn = ?", isbn);
        try {
            if (rs != null && rs.next()) {
                int copiesAvailable = rs.getInt("copiesAvailable");
                db.executeUpdate("UPDATE Books SET copiesAvailable = ? WHERE isbn = ?", copiesAvailable - 1, isbn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Loan by UserID and ISBN.
     * Be careful : a user can have multiple loans for the same book.
     * 
     * @param userID
     * @param copyID
     * @return The loan if it exists, null otherwise.
     */

     public Loan getLoanByUserAndISBN(int userID, String isbn) {
        ResultSet rs = db.executeQuery("SELECT l.loanID FROM Loans l " +
                "JOIN BookCopies bc ON l.copyID = bc.copyID " +
                "WHERE l.userID = ? AND bc.isbn = ? AND l.isReturned = FALSE LIMIT 1", userID, isbn);
        try {
            if (rs != null && rs.next()) {
                int  loanID = rs.getInt("loanID");
                return getLoanByID(loanID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void returnBook(int userID, String isbn) throws Exception {
        Loan loan = getLoanByUserAndISBN(userID, isbn);
        if (loan == null) {
            throw new Exception("Loan not found for user " + userID + " and ISBN " + isbn);
        }

        loan.setReturnDate(LocalDate.now());
        loan.setIsReturned(true);
        loan.update(db);

        BookCopies copy = getCopyByID(loan.getCopyID());
        copy.setLoaned(false);
        copy.update(db);

        // Increase number of copiesAvailable :
        ResultSet rs = db.executeQuery("SELECT copiesAvailable FROM Books WHERE isbn = ?", isbn);
        try {
            if (rs != null && rs.next()) {
                int copiesAvailable = rs.getInt("copiesAvailable");
                db.executeUpdate("UPDATE Books SET copiesAvailable = ? WHERE isbn = ?", copiesAvailable + 1, isbn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String viewLoans(boolean onlyCurrentlyLoaned, boolean onlyOverdueLoans) {
        StringBuilder result = new StringBuilder();
        String query = "SELECT l.loanID, u.name, b.isbn, l.loanDate, l.dueDate, l.isReturned FROM Loans l " +
                "JOIN Users u ON l.userID = u.userID " +
                "JOIN BookCopies bc ON l.copyID = bc.copyID " +
                "JOIN Books b ON bc.isbn = b.isbn " +
                "WHERE IF(?, l.isReturned = FALSE, l.isReturned = TRUE OR l.isReturned = FALSE) AND IF(?, l.dueDate <= ?, TRUE)";
        ResultSet rs = db.executeQuery(query, onlyCurrentlyLoaned, onlyOverdueLoans, LocalDate.now());
        try {
            while (rs.next()) {
                int loanID = rs.getInt("loanID");
                String userName = rs.getString("name");
                String isbn = rs.getString("isbn");
                Boolean isReturned = rs.getBoolean("isReturned");
                LocalDate loanDate = LocalDate.parse(rs.getString("loanDate"));
                LocalDate dueDate = LocalDate.parse(rs.getString("dueDate"));
                result.append("Loan ID: ").append(loanID).append(", User: ").append(userName).append(", ISBN: ")
                        .append(isbn).append(", Loan Date: ").append(loanDate).append(", Due Date: ").append(dueDate)
                        .append(" Returned ? :").append(isReturned).append("\n") ;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public String getUserLoans(int userID) {
        StringBuilder result = new StringBuilder();
        String query = "SELECT l.loanID, b.isbn, l.loanDate, l.dueDate, l.isReturned FROM Loans l " +
                "JOIN BookCopies bc ON l.copyID = bc.copyID " +
                "JOIN Books b ON bc.isbn = b.isbn " +
                "WHERE l.userID = ?";
        ResultSet rs = db.executeQuery(query, userID);
        try {
            while (rs.next()) {
                int loanID = rs.getInt("loanID");
                String isbn = rs.getString("isbn");
                Boolean isReturned = rs.getBoolean("isReturned");
                LocalDate loanDate = LocalDate.parse(rs.getString("loanDate"));
                LocalDate dueDate = LocalDate.parse(rs.getString("dueDate"));
                result.append("Loan ID: ").append(loanID).append(", ISBN: ").append(isbn).append(", Loan Date: ")
                        .append(loanDate).append(", Due Date: ").append(dueDate).append(" Returned ? :").append(isReturned).append("\n") ;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public boolean userExists(int userID) {
        ResultSet rs = db.executeQuery("SELECT 1 FROM Users WHERE userID = ?", userID);
        try {
            return rs != null && rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private User getUserByID(int userID) {
        ResultSet rs = db.executeQuery("SELECT * FROM Users WHERE userID = ?", userID);
        try {
            if (rs != null && rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String address = rs.getString("address");
                User user = new User(name, email, address);
                user.setUserID(userID);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isEmailExists(String email) {
        ResultSet rs = db.executeQuery("SELECT COUNT(*) FROM Users WHERE email = ?", email);
        try {
            if (rs != null && rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isISBNExistsInCopies(String isbn) {
        ResultSet rs = db.executeQuery("SELECT COUNT(*) FROM BookCopies WHERE isbn = ?", isbn);
        try {
            if (rs != null && rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isbnExistsInBNF(String isbn) { //Should be implemented in the future with BNF API
        List<BookApi> bibBooks = apiConnector.searchByISBN("bib", isbn);
        List<BookApi> autBooks = apiConnector.searchByISBN("aut", isbn);
        return !bibBooks.isEmpty() || !autBooks.isEmpty();
    }

    private BookCopies getAvailableCopyByISBN(String isbn) {
        ResultSet rs = db.executeQuery("SELECT copyID FROM BookCopies WHERE isbn = ? AND isLoaned = FALSE LIMIT 1", isbn);
        try {
            if (rs != null && rs.next()) {
                int copyID = rs.getInt("copyID");
                BookCopies copy = new BookCopies(isbn);
                copy.setCopyID(copyID);
                return copy;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BookCopies getCopyByID(int copyID) {
        ResultSet rs = db.executeQuery("SELECT * FROM BookCopies WHERE copyID = ?", copyID);
        try {
            if (rs != null && rs.next()) {
                String isbn = rs.getString("isbn");
                boolean isLoaned = rs.getBoolean("isLoaned");
                BookCopies copy = new BookCopies(isbn);
                copy.setCopyID(copyID);
                copy.setLoaned(isLoaned);
                return copy;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getLoanIDbyUserAndBookID(int userID, int copyID) {
        ResultSet rs = db.executeQuery("SELECT loanID FROM Loans WHERE userID = ? AND copyID = ? AND isReturned = FALSE", userID, copyID);
        try {
            if (rs != null && rs.next()) {
                return rs.getInt("loanID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Loan getLoanByID(int loanID) {
        ResultSet rs = db.executeQuery("SELECT * FROM Loans WHERE loanID = ?", loanID);
        try {
            if (rs != null && rs.next()) {
                int userID = rs.getInt("userID");
                int copyID = rs.getInt("copyID");
                LocalDate loanDate = LocalDate.parse(rs.getString("loanDate"));
                LocalDate dueDate = LocalDate.parse(rs.getString("dueDate"));
                LocalDate returnDate = rs.getString("returnDate") != null ? LocalDate.parse(rs.getString("returnDate")) : null;
                boolean isReturned = rs.getBoolean("isReturned");
                Loan loan = new Loan(userID, copyID);
                loan.setLoanID(loanID);
                loan.setLoanDate(loanDate);
                loan.setDueDate(dueDate);
                loan.setReturnDate(returnDate);
                loan.setIsReturned(isReturned);
                return loan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByEmail(String email) {
        ResultSet rs = db.executeQuery("SELECT * FROM Users WHERE email = ?", email);
        try {
            if (rs != null && rs.next()) {
                int userID = rs.getInt("userID");
                String name = rs.getString("name");
                String address = rs.getString("address");
                User user = new User(name, email, address);
                user.setUserID(userID);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User searchUser(int userID) throws UserNotFoundException {
        User user = getUserByID(userID);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + userID);
        }
        return user;
    }

    public User searchUser(String email) throws UserNotFoundException {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found for email :" + email);
        }
        return user;
    }


    /*public String searchBook(String isbn) throws BookNotFoundException {
        if (isbn.isEmpty()) {
            throw new BookNotFoundException("Book not found: " + isbn);
        }
        List<BookApi> books = apiConnector.searchByISBN("bib", isbn);
        List<BookApi> books2 = apiConnector.searchByISBN("aut", isbn);
        if (!books.isEmpty()) {
            BookApi bookApi = books.getFirst();
            return bookApi.toString();
        }else if (!books2.isEmpty()) {
            BookApi bookApi2 = books2.getFirst();
            return bookApi2.toString();
        }
        throw new BookNotFoundException("Book not found: " + isbn);
    }
    */

    public String searchBook(String searchTerm, String searchType) throws BookNotFoundException {
        if (searchTerm.isEmpty()) {
            throw new BookNotFoundException("Search term cannot be empty.");
        }

        List<BookApi> books;
        List<BookApi> books2;

        switch (searchType.toLowerCase()) {
            case "isbn":
                books = apiConnector.searchByISBN("bib",searchTerm);
                books2 = apiConnector.searchByISBN("aut", searchTerm);
                break;
            case "title":
                books = apiConnector.searchByTitle("bib",searchTerm);
                books2 = apiConnector.searchByTitle("aut", searchTerm);
                break;
            case "author":
                books = apiConnector.searchByAuthor("bib",searchTerm);
                books2 = apiConnector.searchByAuthor("aut", searchTerm);
                break;
            default:
                throw new IllegalArgumentException("Invalid search type: " + searchType);
        }

        if (!books.isEmpty()) {
            BookApi bookApi = books.getFirst(); // assuming the first result is what we want
            return bookApi.toString();
        }else if (!books2.isEmpty()) {
            BookApi bookApi2 = books2.getFirst();
            return bookApi2.toString();
        }

        throw new BookNotFoundException("Book not found: " + searchTerm);
    }

    public String mostLoanedBooksLast30d() {
        StringBuilder result = new StringBuilder();
        String query = "SELECT b.isbn, COUNT(l.loanID) as loanCount FROM Loans l " +
                "JOIN BookCopies bc ON l.copyID = bc.copyID " +
                "JOIN Books b ON bc.isbn = b.isbn " +
                "WHERE l.loanDate >= ? " +
                "GROUP BY b.isbn " +
                "ORDER BY loanCount DESC LIMIT 5";
        ResultSet rs = db.executeQuery(query, LocalDate.now().minusDays(30));
        try {
            while (rs.next()) {
                String isbn = rs.getString("isbn");
                int loanCount = rs.getInt("loanCount");
                result.append("ISBN: ").append(isbn).append(", Loan Count: ").append(loanCount).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    
}

       
