package com.exemple.cybooks.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a book with various details such as ISBN, title, authors, publisher, and more.
 */
public class BookApi {
    private List<String> identifiers; // List of identifiers for the book
    private String isbn; // ISBN of the book
    private String title; // Title of the book
    private List<String> authors; // List of authors of the book
    private String publisher; // Publisher of the book
    private String date; // Publication date of the book
    private List<String> descriptions; // Descriptions of the book
    private String format; // Format of the book
    private List<String> languages; // Languages in which the book is available
    private List<String> types; // Types of the book
    private List<String> rights; // Rights information of the book

    /**
     * Constructor to initialize the fields with default values.
     */
    public BookApi() {
        identifiers = new ArrayList<>();
        title = "";
        isbn = "";
        authors = new ArrayList<>();
        publisher = "";
        date = "";
        descriptions = new ArrayList<>();
        format = "";
        languages = new ArrayList<>();
        types = new ArrayList<>();
        rights = new ArrayList<>();
    }

    /**
     * Gets the list of identifiers.
     *
     * @return the list of identifiers
     */
    public List<String> getIdentifiers() {
        return identifiers;
    }

    /**
     * Sets the list of identifiers.
     *
     * @param identifiers the list of identifiers to set
     */
    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    /**
     * Gets the title of the book.
     *
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the list of authors.
     *
     * @return the list of authors
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * Sets the list of authors.
     *
     * @param authors the list of authors to set
     */
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    /**
     * Gets the publisher of the book.
     *
     * @return the publisher of the book
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher of the book.
     *
     * @param publisher the publisher to set
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Gets the publication date of the book.
     *
     * @return the publication date of the book
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the publication date of the book.
     *
     * @param date the publication date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the list of descriptions.
     *
     * @return the list of descriptions
     */
    public List<String> getDescriptions() {
        return descriptions;
    }

    /**
     * Sets the list of descriptions.
     *
     * @param descriptions the list of descriptions to set
     */
    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    /**
     * Gets the format of the book.
     *
     * @return the format of the book
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the format of the book.
     *
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Gets the list of languages.
     *
     * @return the list of languages
     */
    public List<String> getLanguages() {
        return languages;
    }

    /**
     * Sets the list of languages.
     *
     * @param languages the list of languages to set
     */
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    /**
     * Gets the list of types.
     *
     * @return the list of types
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     * Sets the list of types.
     *
     * @param types the list of types to set
     */
    public void setTypes(List<String> types) {
        this.types = types;
    }

    /**
     * Gets the list of rights.
     *
     * @return the list of rights
     */
    public List<String> getRights() {
        return rights;
    }

    /**
     * Sets the list of rights.
     *
     * @param rights the list of rights to set
     */
    public void setRights(List<String> rights) {
        this.rights = rights;
    }

    /**
     * Sets the ISBN of the book.
     *
     * @param isbn the ISBN to set
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the ISBN of the book.
     *
     * @return the ISBN of the book
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Returns a string representation of the book, including its title, authors, ISBN, publisher, and publication date.
     *
     * @return a string representation of the book
     */
    @Override
    public String toString() {
        StringBuilder authors = new StringBuilder();
        for (String author : this.getAuthors()) {
            authors.append(author).append(";");
        }
        return "Title: " + this.getTitle() + "\nAuthors: " + authors + "\nISBN: " + this.getIsbn() + "\nPublisher: " + this.getPublisher() + "\nDate: " + this.getDate();
    }
}
