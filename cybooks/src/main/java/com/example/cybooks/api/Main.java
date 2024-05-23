package com.example.cybooks.api;

import java.util.List;

import com.example.cybooks.model.BookApi;

/**
 * A class containing the main method to demonstrate the usage of the ApiConnector class.
 */
public class Main {

    /**
     * The main method to demonstrate the usage of the ApiConnector class.
     *
     * @param args The command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Creating an instance of ApiConnector
        ApiConnector apiConnector = new ApiConnector();
        // Searching for books by title using the "bib" record type and "les misérables" title
        List<BookApi> bookApis = apiConnector.searchByTitle("bib", "les misérables");
        // Printing the title and ISBN of each book found
        for (BookApi bookApi : bookApis) {
            System.out.println(bookApi.getTitle());
            System.out.println(bookApi.getIsbn());
        }
    }
}
