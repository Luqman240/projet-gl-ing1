package cybooks.model;

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
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "cybooks";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 
    private Connection connection;

    /**
     * Starts the database server and establishes a connection.
     * Creates the database if it doesn't exist and initializes the database tables if they do not already exist.
     */
    public void startServer() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            createDatabaseIfNotExists();
            connection = DriverManager.getConnection(JDBC_URL + DATABASE_NAME, USER, PASSWORD);
            System.out.println("Database started and connected.");
            initializeDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the database if it doesn't exist.
     */
    private void createDatabaseIfNotExists() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            stmt.executeUpdate(createDatabaseQuery);
            System.out.println("Database created or already exists.");
        } catch (SQLException e) {
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
                "address TEXT) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;";

        String createBooksTable = "CREATE TABLE IF NOT EXISTS Books (" +
                "isbn VARCHAR(13) PRIMARY KEY, " +
                "copiesAvailable INT);";

        String createBookCopiesTable = "CREATE TABLE IF NOT EXISTS BookCopies (" +
                "copyID INT AUTO_INCREMENT PRIMARY KEY, " +
                "isbn VARCHAR(13), " +
                "isLoaned BOOLEAN DEFAULT FALSE, " +
                "FOREIGN KEY (isbn) REFERENCES Books(isbn) ON DELETE CASCADE);";

        String createLoansTable = "CREATE TABLE IF NOT EXISTS Loans (" +
                "loanID INT AUTO_INCREMENT PRIMARY KEY, " +
                "userID INT, " +
                "copyID INT, " +
                "loanDate DATE, " +
                "numberOfDays INT, " +
                "dueDate DATE, " +
                "returnDate DATE, " +
                "isReturned BOOLEAN DEFAULT FALSE, " +
                "FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE, " +
                "FOREIGN KEY (copyID) REFERENCES BookCopies(copyID) ON DELETE CASCADE);";

        executeUpdate(createUsersTable);
        executeUpdate(createBooksTable);
        executeUpdate(createBookCopiesTable);
        executeUpdate(createLoansTable);
    }

    /**
     * Executes a SQL update statement.
     *
     * @param query The SQL statement to execute.
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
     * Executes a SQL insert statement and returns the generated key.
     *
     * @param query The SQL insert statement to execute.
     * @param params The parameters to set in the prepared statement.
     * @return The generated key, or -1 if no key was generated.
     */
    public int executeInsert(String query, Object... params) {
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
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
     * @param query The SQL query to execute.
     * @param params The parameters to set in the prepared statement.
     * @return The result set of the query.
     */
    public ResultSet executeQuery(String query, Object... params) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
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
