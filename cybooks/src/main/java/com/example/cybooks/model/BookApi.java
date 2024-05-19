package com.example.cybooks.model;

import java.util.ArrayList;
import java.util.List;

public class BookApi {
    private List<String> identifiers;
    private String isbn;
    private String title;
    private List<String> authors;
    private String publisher;
    private String date;
    private List<String> descriptions;
    private String format;
    private List<String> languages;
    private List<String> types;
    private List<String> rights;

    public BookApi() {
        identifiers = new ArrayList<>();
        title = "";
        isbn = "";
        authors = new ArrayList<>();
        publisher = "";
        date = "";
        descriptions = new ArrayList<>();
        format="";
        languages=new ArrayList<>();
        types=new ArrayList<>();
        rights=new ArrayList<>();
    }
    // Getters and Setters
    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifier) {
        this.identifiers = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getRights() {
        return rights;
    }

    public void setRights(List<String> rights) {
        this.rights = rights;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }
}
