package com.example.cybooks.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class LibraryManager {
    private DataBase db;

    public LibraryManager(DataBase db) {
        this.db = db;
    }

    public void closeDatabase() {
        db.stopServer();
    }

    public String registerUser(String name, String email, String address) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Error: Invalid email format.";
        }

        if (isEmailExists(email)) {
            return "Error: Email already exists.";
        }

        try {
            User user = new User(name, email, address);
            user.register(db);
            return "User registered successfully: " + user;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String deleteUser(int userID) {
        User user = getUserByID(userID);
        if (user == null) {
            return "User not found.";
        }

        user.delete(db);
        return "User deleted successfully.";
    }

    public String updateUser(int userID, String name, String email, String address) {
        User user = getUserByID(userID);
        if (user == null) {
            return "User not found.";
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Error: Invalid email format.";
        }

        if (isEmailExists(email) && !email.equals(user.getEmail())) {
            return "Error: Email already exists.";
        }

        try {
            user.setName(name);
            user.setEmail(email);
            user.setAddress(address);
            user.update(db);
            return "User updated successfully: " + user;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
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

    public String addBook(String isbn, int copiesAvailable) {
        try {
            Book book = new Book(isbn, copiesAvailable);
            book.register(db);
            for (int i = 0; i < copiesAvailable; i++) {
                BookCopy copy = new BookCopy(isbn);
                copy.register(db);
            }
            return "Book added successfully: " + book;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String loanBook(int userID, String isbn, int numberOfDays) {
        User user = getUserByID(userID);
        if (user == null) {
            return "User not found.";
        }

        BookCopy copy = getAvailableCopyByISBN(isbn);
        if (copy == null) {
            return "No available copies for this ISBN.";
        }

        try {
            Loan loan = new Loan(userID, copy.getCopyID(), numberOfDays);
            loan.register(db);
            copy.setLoaned(true);
            copy.update(db);
            return "Loan added successfully: " + loan;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String returnBook(int loanID) {
        Loan loan = getLoanByID(loanID);
        if (loan == null) {
            return "Loan not found.";
        }

        try {
            loan.setReturnDate(LocalDate.now());
            loan.setReturned(true);
            loan.update(db);

            BookCopy copy = getCopyByID(loan.getCopyID());
            copy.setLoaned(false);
            copy.update(db);

            return "Book returned successfully.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String viewLoans() {
        StringBuilder result = new StringBuilder();
        String query = "SELECT l.loanID, u.name, b.isbn, l.loanDate, l.dueDate FROM Loans l " +
                "JOIN Users u ON l.userID = u.userID " +
                "JOIN BookCopies bc ON l.copyID = bc.copyID " +
                "JOIN Books b ON bc.isbn = b.isbn " +
                "WHERE l.isReturned = FALSE";
        ResultSet rs = db.executeQuery(query);
        try {
            while (rs.next()) {
                int loanID = rs.getInt("loanID");
                String userName = rs.getString("name");
                String isbn = rs.getString("isbn");
                LocalDate loanDate = LocalDate.parse(rs.getString("loanDate"));
                LocalDate dueDate = LocalDate.parse(rs.getString("dueDate"));
                result.append("Loan ID: ").append(loanID).append(", User: ").append(userName).append(", ISBN: ")
                        .append(isbn).append(", Loan Date: ").append(loanDate).append(", Due Date: ").append(dueDate).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    
    private BookCopy getAvailableCopyByISBN(String isbn) {
        ResultSet rs = db.executeQuery("SELECT copyID FROM BookCopies WHERE isbn = ? AND isLoaned = FALSE LIMIT 1", isbn);
        try {
            if (rs != null && rs.next()) {
                int copyID = rs.getInt("copyID");
                BookCopy copy = new BookCopy(isbn);
                copy.setCopyID(copyID);
                return copy;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BookCopy getCopyByID(int copyID) {
        ResultSet rs = db.executeQuery("SELECT * FROM BookCopies WHERE copyID = ?", copyID);
        try {
            if (rs != null && rs.next()) {
                String isbn = rs.getString("isbn");
                boolean isLoaned = rs.getBoolean("isLoaned");
                BookCopy copy = new BookCopy(isbn);
                copy.setCopyID(copyID);
                copy.setLoaned(isLoaned);
                return copy;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Loan getLoanByID(int loanID) {
        ResultSet rs = db.executeQuery("SELECT * FROM Loans WHERE loanID = ?", loanID);
        try {
            if (rs != null && rs.next()) {
                int userID = rs.getInt("userID");
                int copyID = rs.getInt("copyID");
                LocalDate loanDate = LocalDate.parse(rs.getString("loanDate"));
                int numberOfDays = rs.getInt("numberOfDays");
                LocalDate dueDate = LocalDate.parse(rs.getString("dueDate"));
                LocalDate returnDate = rs.getString("returnDate") != null ? LocalDate.parse(rs.getString("returnDate")) : null;
                boolean isReturned = rs.getBoolean("isReturned");
                Loan loan = new Loan(userID, copyID, numberOfDays);
                loan.setLoanID(loanID);
                loan.setLoanDate(loanDate);
                loan.setDueDate(dueDate);
                loan.setReturnDate(returnDate);
                loan.setReturned(isReturned);
                return loan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
