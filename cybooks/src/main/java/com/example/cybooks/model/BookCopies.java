package com.example.cybooks.model;

/**
 * Represents a copy of a book in the library.
 * This class contains information such as the copy ID and ISBN.
 */
public class BookCopies {
    private int copyID;
    private String isbn;
    private boolean isLoaned;

    /**
     * Constructs a new book copy with the specified information.
     *
     * @param isbn The ISBN of the book.
     */
    public BookCopies(String isbn) {
        this.isbn = isbn;
        this.isLoaned = false;
    }

    /**
     * Sets the copy ID of the book copy.
     * @return The copy ID of the book copy.
     */

    public int getCopyID() {
        return copyID;
    }

    /**
     * Sets the copy ID of the book copy.
     * @param copyID The copy ID of the book copy.
     */

    public void setCopyID(int copyID) {
        this.copyID = copyID;
    }

    /**
     * Gets the ISBN of the book copy.
     * @return The ISBN of the book copy.
     */

    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book copy.
     * @param isbn The ISBN of the book copy.
     */

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Checks if the book copy is loaned.
     * @return True if the book copy is loaned, false otherwise.
     */

    public boolean isLoaned() {
        return isLoaned;
    }

    /**
     * Sets the loan status of the book copy.
     * @param loaned True if the book copy is loaned, false otherwise.
     */

    public void setLoaned(boolean loaned) {
        isLoaned = loaned;
    }

    /**
     * Returns a string representation of the book copy.
     * @return A string representation of the book copy.
     */

    @Override
    public String toString() {
        return "BookCopy{" +
                "copyID=" + copyID +
                ", isbn='" + isbn + '\'' +
                ", isLoaned=" + isLoaned +
                '}';
    }

    /**
     * Registers the book copy in the database.
     * @param db The database to register the book copy in.
     */

    public void register(DataBase db) {
        String query = "INSERT INTO BookCopies (isbn) VALUES (?)";
        int generatedID = db.executeInsert(query, this.isbn);
        if (generatedID != -1) {
            this.copyID = generatedID;
        }
    }

    /**
     * Updates the book copy information in the database.
     * @param db The database to update the book copy in.
     */

    public void update(DataBase db) {
        String query = "UPDATE BookCopies SET isLoaned = ? WHERE copyID = ?";
        db.executeUpdate(query, this.isLoaned, this.copyID);
    }

    /**
     * Deletes the book copy from the database.
     * @param db The database to delete the book copy from.
     */

    public void delete(DataBase db) {
        String query = "DELETE FROM BookCopies WHERE copyID = ?";
        db.executeUpdate(query, this.copyID);
    }
}
