package com.example.cybooks.model;

/**
 * Represents the book that has been lend to the user.
 * It contains information on the book such as its ID, ISBN, and copies available.
 * It also provides getter/setter methods.
 */

public class Book {
    private int bookID;
    private String isbn;
    private int copiesAvailable;

    /**
     * 
     * @param bookID The book's ID.
     * @param isbn The book's ISBN.
     * @param copiesAvailable The number of copies available for the book.
     */

    public Book(String isbn, int copiesAvailable) {
        this.isbn = isbn;
        this.copiesAvailable = copiesAvailable;
    }

    /**
     * Returns the book's ID.
     * 
     * @return The book's ID.
     */
    public int getBookID() {
        return bookID;
    }

    /**
     * Sets the book's ID.
     * This is used internally after retrieving from the database.
     * 
     * @param bookID The book's ID.
     */
    public void setBookID(int bookID) {
        this.bookID = bookID;
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
                "bookID=" + bookID +
                ", isbn='" + isbn + '\'' +
                ", copiesAvailable=" + copiesAvailable +
                '}';
    }
    
}
