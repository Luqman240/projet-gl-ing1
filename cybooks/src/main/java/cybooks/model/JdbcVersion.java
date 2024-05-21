package cybooks.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcVersion {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:h2:~/cybooks"; // Remplacez par votre URL de connexion JDBC
        String user = "root"; // Remplacez par votre utilisateur
        String password = ""; // Remplacez par votre mot de passe

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("JDBC Driver Name: " + metaData.getDriverName());
            System.out.println("JDBC Driver Version: " + metaData.getDriverVersion());
            System.out.println("JDBC Major Version: " + metaData.getJDBCMajorVersion());
            System.out.println("JDBC Minor Version: " + metaData.getJDBCMinorVersion());
            System.out.println("Database Product Name: " + metaData.getDatabaseProductName());
            System.out.println("Database Product Version: " + metaData.getDatabaseProductVersion());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
