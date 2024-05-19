package com.example.cybooks.model;

//import com.example.cybooks.model.DataBase;
//import com.example.cybooks.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Test class to demonstrate the usage of the User and DataBase classes.
 */

public class Test {
    public static void main(String[] args) {
        // Create and start the database
        DataBase db = new DataBase();
        db.startServer();

        // Test: Register a new user
        User user = new User("Younes Ben", "youns@gmail.com", "1 chemin du roi");
        user.register(db);
        System.out.println("Registered user: " + user);

        // Test: Fetch the user from the database
        User fetchedUser = fetchUserById(db, user.getUserID());
        System.out.println("Fetched user: " + fetchedUser);

        // Test: Update the user
        user.setName("Youyou ben");
        user.setEmail("youyou.ben@gmail.com");
        user.setAddress("1 chemin de la reine");
        user.update(db);
        System.out.println("Updated user: " + user);

        // Test: Fetch the updated user
        fetchedUser = fetchUserById(db, user.getUserID());
        System.out.println("Fetched updated user: " + fetchedUser);

        // Test: Delete the user
        user.delete(db);
        System.out.println("Deleted user with ID: " + user.getUserID());

        // Test: Try to fetch the deleted user
        fetchedUser = fetchUserById(db, user.getUserID());
        System.out.println("Fetched after delete (should be null): " + fetchedUser);

        // Stop the database
        db.stopServer();
    }

    private static User fetchUserById(DataBase db, int userID) {
        String query = "SELECT * FROM Users WHERE userID = ?";
        try (ResultSet rs = db.executeQuery(query, String.valueOf(userID))) {
            if (rs != null && rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String address = rs.getString("address");
                User user = new User(name, email, address);
                user.setUserID(rs.getInt("userID"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
