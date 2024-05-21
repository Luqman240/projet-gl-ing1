package cybooks.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class demonstrates how to retrieve and display JDBC driver and database information.
 */
public class JdbcVersion {
    /**
     * The main method establishes a connection to the database and retrieves metadata about the JDBC driver and database.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:h2:~/cybooks"; // Replace with your JDBC connection URL
        String user = "root"; // Replace with your database username
        String password = ""; // Replace with your database password

        // Establish a connection to the database
        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            // Retrieve metadata about the database
            DatabaseMetaData metaData = connection.getMetaData();
            // Print JDBC driver name
            System.out.println("JDBC Driver Name: " + metaData.getDriverName());
            // Print JDBC driver version
            System.out.println("JDBC Driver Version: " + metaData.getDriverVersion());
            // Print JDBC major version
            System.out.println("JDBC Major Version: " + metaData.getJDBCMajorVersion());
            // Print JDBC minor version
            System.out.println("JDBC Minor Version: " + metaData.getJDBCMinorVersion());
            // Print database product name
            System.out.println("Database Product Name: " + metaData.getDatabaseProductName());
            // Print database product version
            System.out.println("Database Product Version: " + metaData.getDatabaseProductVersion());
        } catch (SQLException e) {
            // Print stack trace if a SQL exception occurs
            e.printStackTrace();
        }
    }
}
