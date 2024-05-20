package com.example.cybooks.model;

import java.util.regex.Pattern;

/**
 * Represents a library user.
 * This class contains user information such as ID, name, email, and address.
 * It also provides methods to manage users in the database.
 */
public class User {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"
    );

    private int userID; // Will be auto-generated by the database
    private String name;
    private String email;
    private String address;

    /**
     * Constructs a new user with the specified information.
     * The userID is auto-generated by the database.
     * 
     * @param userID The user's ID.
     * @param name    The user's name.
     * @param email   The user's email.
     * @param address The user's address.
     */
    public User(String name, String email, String address) {
        this.name = name;
        setEmail(email);
        this.address = address;
    }

    public User(int userID, String name, String email, String address) {
        this.name = name;
        setEmail(email);
        this.address = address;
        this.userID = userID;
    }

    /**
     * Returns the user's ID.
     *
     * @return The user's ID.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Sets the user's ID.
     * This is used internally after retrieving from the database.
     *
     * @param userID The user's ID.
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * Gets the user's name.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name The user's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's email.
     *
     * @return The user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email The user's email.
     * @throws IllegalArgumentException if the email format is invalid.
     */
    public void setEmail(String email) throws IllegalArgumentException {
        if(!EMAIL_PATTERN.matcher(email).matches()){
            throw new IllegalArgumentException("Invalid email format :" + email);
        }
        this.email = email;
    }

    /**
     * Gets the user's address.
     *
     * @return The user's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the user's address.
     *
     * @param address The user's address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Registers a new user in the database.
     * The userID is auto-generated by the database and set in the User object.
     *
     * @param db The DataBase object used to execute the query.
     */
    public void register(DataBase db) {
        String query = "INSERT INTO Users (name, email, address) VALUES (?, ?, ?)";
        int generatedID = db.executeInsert(query, this.name, this.email, this.address);
        if (generatedID != -1) {
            this.userID = generatedID;
        }
    }

    /**
     * Updates the user's information in the database.
     *
     * @param db The DataBase object used to execute the query.
     */
    public void update(DataBase db) {
        String query = "UPDATE Users SET name = ?, email = ?, address = ? WHERE userID = ?";
        db.executeUpdate(query, this.name, this.email, this.address, this.userID);
    }

    /**
     * Deletes the user from the database.
     *
     * @param db The DataBase object used to execute the query.
     */
    public void delete(DataBase db) {
        String query = "DELETE FROM Users WHERE userID = ?";
        db.executeUpdate(query, this.userID);
    }

    /**
     * Returns a string representation of the user.
     *
     * @return A string representation of the user.
     */
    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
