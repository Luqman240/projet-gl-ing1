package com.example.cybooks.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.example.cybooks.api.ApiConnector;
import com.example.cybooks.exception.BookNotFoundException;
import com.example.cybooks.exception.EmailAlreadyExistsException;
import com.example.cybooks.exception.InvalidEmailFormatException;
import com.example.cybooks.exception.NoCopyAvailableException;
import com.example.cybooks.exception.UserHasLoansException;
import com.example.cybooks.exception.UserNotFoundException;
import com.example.cybooks.model.Book;
import com.example.cybooks.model.BookApi;
import com.example.cybooks.model.BookCopies;
import com.example.cybooks.model.DataBase;
import com.example.cybooks.model.Loan;
import com.example.cybooks.model.User;

/**
 * Manages the operations related to the library, including user registration,
 * book loans, and database interactions.
 */
public class LibraryManager {
    private final DataBase db;
    private final ApiConnector apiConnector;

    /**
     * Constructs a LibraryManager with the given database connection.
     *
     * @param db the database connection object
     */
    public LibraryManager(DataBase db) {
        this.db = db;
        this.apiConnector = new ApiConnector();
    }

    /**
     * Closes the database connection.
     */
    public void closeDatabase() {
        db.stopServer();    
    }

    /**
     * Registers a new user in the library system.
     *
     * @param name    the name of the user
     * @param email   the email address of the user
     * @param address the address of the user
     * @throws InvalidEmailFormatException if the email format is invalid
     * @throws EmailAlreadyExistsException if the email already exists in the system
     */
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

    /**
     * Updates the information of an existing user.
     *
     * @param userID  the ID of the user to update
     * @param name    the new name of the user
     * @param email   the new email address of the user
     * @param address the new address of the user
     * @throws UserNotFoundException       if the user is not found
     * @throws InvalidEmailFormatException if the email format is invalid
     * @throws EmailAlreadyExistsException if the email already exists in the system
     */
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

    /**
     * Deletes a user from the library system.
     *
     * @param userID the ID of the user to delete
     * @throws UserNotFoundException  if the user is not found
     * @throws UserHasLoansException if the user has outstanding loans
     */
    public void deleteUser(int userID) throws UserNotFoundException, UserHasLoansException {
        User user = getUserByID(userID);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + userID);
        }
        if (isLoansExistsForUsers(userID)) {
            throw new UserHasLoansException("User has loans and cannot be deleted");
        }

        user.delete(db);
    }

    /**
     * Adds a new book to the library's collection.
     *
     * @param isbn            the ISBN of the book
     * @param copiesAvailable the number of copies available
     */
    public void addBook(String isbn, int copiesAvailable) {
        Book book = new Book(isbn, copiesAvailable);
        book.register(db);
        for (int i = 0; i < copiesAvailable; i++) {
            BookCopies copy = new BookCopies(isbn);
            copy.register(db);
        }
    }

    /**
     * Loans a book to a user.
     *
     * @param userID the ID of the user
     * @param isbn   the ISBN of the book
     * @throws UserNotFoundException  if the user is not found
     * @throws NoCopyAvailableException if no copy of the book is available
     */
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

        // Decrease number of copies available
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
     * Retrieves a loan by user ID and ISBN.
     * Note: a user can have multiple loans for the same book.
     *
     * @param userID the ID of the user
     * @param isbn   the ISBN of the book
     * @return the loan if it exists, null otherwise
     */
    public Loan getLoanByUserAndISBN(int userID, String isbn) {
        ResultSet rs = db.executeQuery("SELECT l.loanID FROM Loans l " +
                "JOIN BookCopies bc ON l.copyID = bc.copyID " +
                "WHERE l.userID = ? AND bc.isbn = ? AND l.isReturned = FALSE LIMIT 1", userID, isbn);
        try {
            if (rs != null && rs.next()) {
                int loanID = rs.getInt("loanID");
                return getLoanByID(loanID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a book that was loaned out.
     *
     * @param userID the ID of the user
     * @param isbn   the ISBN of the book
     * @throws Exception if the loan is not found or another error occurs
     */
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

        // Increase number of copies available
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

    /**
     * Views loans in the library.
     *
     * @param onlyCurrentlyLoaned whether to view only currently loaned books
     * @param onlyOverdueLoans    whether to view only overdue loans
     * @return a string representation of the loans
     */
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

    /**
     * Retrieves the loans of a user identified by the given user ID.
     *
     * @param userID The ID of the user whose loans are to be retrieved.
     * @return A string representing the user's loans.
    */
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

    /**
     * Checks if a user with the specified ID exists in the database.
     *
     * @param userID The ID of the user to check for existence.
     * @return True if the user exists, false otherwise.
     */
    public boolean userExists(int userID) {
        ResultSet rs = db.executeQuery("SELECT 1 FROM Users WHERE userID = ?", userID);
        try {
            return rs != null && rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves the user information associated with the given user ID.
     *
     * @param userID The ID of the user to retrieve information for.
     * @return The user object containing the user's information, or null if not found.
     */
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
    
    /**
     * Checks if an email exists in the database.
     *
     * @param email The email to check for existence.
     * @return True if the email exists, false otherwise.
     */
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

    /**
     * Checks if an ISBN exists in the book copies table of the database.
     *
     * @param isbn The ISBN to check for existence.
     * @return True if the ISBN exists, false otherwise.
     */
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

    /**
     * Checks if a given ISBN exists either in the "bib" or "aut" category of the BNF API.
     *
     * @param isbn The ISBN to check for existence.
     * @return True if the ISBN exists in either category, false otherwise.
     */
    public boolean isbnExistsInBNF(String isbn) { //Should be implemented in the future with BNF API
        List<BookApi> bibBooks = apiConnector.searchByISBN("bib", isbn);
        List<BookApi> autBooks = apiConnector.searchByISBN("aut", isbn);
        return !bibBooks.isEmpty() || !autBooks.isEmpty();
    }

    /**
     * Retrieves an available book copy by ISBN from the database.
     *
     * @param isbn The ISBN of the book copy to retrieve.
     * @return The available book copy, or null if not found.
     */
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

    /**
     * Retrieves a book copy by its ID from the database.
     *
     * @param copyID The ID of the book copy to retrieve.
     * @return The book copy with the given ID, or null if not found.
     */
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

    /**
     * Retrieves the loan ID associated with a user and book copy from the database.
     *
     * @param userID The ID of the user.
     * @param copyID The ID of the book copy.
     * @return The loan ID if found, or -1 if not found.
     */
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

    /**
     * Retrieves a loan by its ID from the database.
     *
     * @param loanID The ID of the loan to retrieve.
     * @return The loan with the given ID, or null if not found.
     */
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

    /**
     * Retrieves a user by their email address from the database.
     *
     * @param email The email address of the user to retrieve.
     * @return The user with the given email address, or null if not found.
     */
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

    /**
     * Searches for a user by their ID and throws a UserNotFoundException if not found.
     *
     * @param userID The ID of the user to search for.
     * @return The user with the given ID.
     * @throws UserNotFoundException If the user with the given ID is not found.
     */
    public User searchUser(int userID) throws UserNotFoundException {
        User user = getUserByID(userID);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + userID);
        }
        return user;
    }

    /**
     * Searches for a user by their email address and throws a UserNotFoundException if not found.
     *
     * @param email The email address of the user to search for.
     * @return The user with the given email address.
     * @throws UserNotFoundException If the user with the given email address is not found.
     */
    public User searchUser(String email) throws UserNotFoundException {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found for email :" + email);
        }
        return user;
    }

    /**
     * Searches for a book based on a search term and a search type.
     *
     * @param searchTerm The term to search for. This could be the title, author, or ISBN of the book.
     * @param searchType The type of search to perform. It can be "isbn", "title", or "author".
     * @return A string representing the found books. Each book is separated by a newline.
     * @throws BookNotFoundException If no book is found with the given search term.
     * @throws IllegalArgumentException If an invalid search type is provided.
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
            case "date":
                books = apiConnector.searchByDate("bib",searchTerm);
                books2 = apiConnector.searchByDate("aut", searchTerm);
                break;
            default:
                throw new IllegalArgumentException("Invalid search type: " + searchType);
        }
    
        StringBuilder booksString = new StringBuilder();
        if (!books.isEmpty()) {
            List<BookApi> firstTwoBooks = books.stream().limit(50).collect(Collectors.toList());
            for (BookApi book : firstTwoBooks) {
                booksString.append(book.toString());
            }
        } else if (!books2.isEmpty()) {
            List<BookApi> bookApi2 = books2.stream().limit(50).collect(Collectors.toList());
            for (BookApi book : bookApi2) {
                booksString.append(book.toString());
            }
        } else {
            throw new BookNotFoundException("Book not found: " + searchTerm);
        }
    
        return booksString.toString();
    }

    /**
     * Searches for a book based on a search term and a search type.
     *
     * @param searchTerm The term to search for. This could be the title, author, or ISBN of the book.
     * @param searchType The type of search to perform. It can be "isbn", "title", or "author".
     * @return A string representing the found books. Each book is separated by a newline.
     * @throws BookNotFoundException If no book is found with the given search term.
     * @throws IllegalArgumentException If an invalid search type is provided.
     */
    public String searchBook2(String searchTerm, String searchType) throws BookNotFoundException {
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
            case "date":
                books = apiConnector.searchByDate("bib",searchTerm);
                books2 = apiConnector.searchByDate("aut", searchTerm);
                break;
            default:
                throw new IllegalArgumentException("Invalid search type: " + searchType);
        }
    
        StringBuilder booksString = new StringBuilder();
        if (!books.isEmpty()) {
            List<BookApi> firstTwoBooks = books.stream().limit(50).collect(Collectors.toList());
            for (BookApi book : firstTwoBooks) {
                booksString.append(book.toString2());
            }
        } else if (!books2.isEmpty()) {
            List<BookApi> bookApi2 = books2.stream().limit(50).collect(Collectors.toList());
            for (BookApi book : bookApi2) {
                booksString.append(book.toString2());
            }
        } else {
            throw new BookNotFoundException("Book not found: " + searchTerm);
        }
    
        return booksString.toString();
    }

    /**
     * Retrieves the most loaned books in the last 30 days from the database.
     *
     * @return A string containing information about the most loaned books.
     */
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

    /**
     * Checks if there are existing loans for a given user.
     *
     * @param userID The ID of the user.
     * @return True if loans exist for the user, false otherwise.
     */
    private boolean isLoansExistsForUsers(int userID) {
        ResultSet rs = db.executeQuery("SELECT COUNT(*) FROM Loans WHERE userID = ? AND isReturned = FALSE", userID);
        try {
            if (rs != null && rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

       
