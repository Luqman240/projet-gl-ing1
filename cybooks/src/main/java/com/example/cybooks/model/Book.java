package com.example.cybooks.model;

/**
 * Represents the book that has been lend to the user.
 * It contains information on the book such as its ID, ISBN, and copies available.
 * It also provides getter/setter methods.
 */

public class Book {
    private String isbn;
    private int copiesAvailable;

    /**
     * Constructs a new book with the specified information.
     * 
     * @param isbn The book's ISBN.
     * @param copiesAvailable The number of copies available for the book.
     */

    public Book(String isbn, int copiesAvailable) {
        this.isbn = isbn;
        this.copiesAvailable = copiesAvailable;
    }

    /**
     * Gets the book's ISBN.
     * 
     * @return The book's ISBN.
     */
    public String getIsbn() {
        return isbn;
    }
    
    /**
     * Sets the book's ISBN.
     * 
     * @param isbn The book's ISBN.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the number of copies available for the book.
     * 
     * @return The number of copies available.
     */

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    /**
     * Sets the number of copies available for the book.
     * 
     * @param copiesAvailable The number of copies available.
     */

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", copiesAvailable=" + copiesAvailable +
                '}';
    }

    /**
     * Registers the book in the database.
     * 
     * @param db The database to register the book in.
     */
    
    public void register(DataBase db) {
        String query = "INSERT INTO Books (isbn, copiesAvailable) VALUES (?, ?)";
        db.executeInsert(query, this.isbn, this.copiesAvailable);
    }

    /**
     * Updates the book's information in the database.
     * 
     * @param db The database to update the book in.
     */

    public void update(DataBase db) {
        String query = "UPDATE Books SET copiesAvailable = ? WHERE isbn = ?";
        db.executeUpdate(query, this.copiesAvailable, this.isbn);
    }

    /**
     * Deletes the book from the database.
     * 
     * @param db The database to delete the book from.
     */

    public void delete(DataBase db) {
        String query = "DELETE FROM Books WHERE isbn = ?";
        db.executeUpdate(query, this.isbn);
    }
}
