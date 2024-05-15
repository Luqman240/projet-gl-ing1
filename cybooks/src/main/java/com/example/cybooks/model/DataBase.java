package com.example.cybooks.model;

import java.sql.Connection; 
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the database connection and operations.
 */
public class DataBase {
    private static final String JDBC_URL = "jdbc:h2:~/cybooks"; //Database's name : cybooks.
    private static final String USER = "root"; // Username : required for database connection.
    private static final String PASSWORD = ""; // Password : required for database connection.
    private Connection connection;

    /**
     * Starts the database server and establishes a connection.
     * Initializes the database tables if they do not already exist.
     */
    public void startServer() {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            System.out.println("Database started and connected.");
            initializeDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the database tables if they do not already exist.
     */
    private void initializeDatabase() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                "userID INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "email VARCHAR(255) UNIQUE, " +
                "address TEXT);";

        String createBooksTable = "CREATE TABLE IF NOT EXISTS Books (" +
                "bookID VARCHAR(50) PRIMARY KEY, " +
                "isbn VARCHAR(13), " +
                "copiesAvailable INT);";

        String createLoansTable = "CREATE TABLE IF NOT EXISTS Loans (" +
                "loanID VARCHAR(50) PRIMARY KEY, " +
                "userID INT, " +
                "bookID VARCHAR(50), " +
                "loanDate DATE, " +
                "dueDate DATE, " +
                "returnDate DATE, " +
                "FOREIGN KEY (userID) REFERENCES Users(userID), " +
                "FOREIGN KEY (bookID) REFERENCES Books(bookID));";

        executeUpdate(createUsersTable);
        executeUpdate(createBooksTable);
        executeUpdate(createLoansTable);
    }

    /**
     * Executes a SQL update statement.
     *
     * @param query The SQL statement to execute.
     */
    public void executeUpdate(String query) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            System.out.println("Query executed: " + query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes a SQL update statement with parameters.
     *
     * @param query  The SQL update statement to execute.
     * @param params The parameters to set in the prepared statement.
     */
    public void executeUpdate(String query, Object... params) {
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            pstmt.executeUpdate();
            System.out.println("Query executed: " + query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes a SQL insert statement and returns the generated key. Used to add a new user in the database and get his userID.
     *
     * @param query  The SQL insert statement to execute.
     * @param params The parameters to set in the prepared statement.
     * @return The generated key, or -1 if no key was generated.
     */
    public int executeInsert(String query, String... params) {
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Executes a SQL query and returns the result set.
     *
     * @param query  The SQL query to execute.
     * @param params The parameters to set in the prepared statement.
     * @return The result set of the query.
     */
    public ResultSet executeQuery(String query, String... params) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Closes the database connection.
     */
    public void stopServer() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database stopped.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the database connection.
     *
     * @return The database connection.
     */
    public Connection getConnection() {
        return connection;
    }
}
