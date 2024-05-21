package com.example.cybooks.api;

import com.example.cybooks.model.BookApi;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ApiConnector apiConnector = new ApiConnector();
        List<BookApi> bookApis = apiConnector.searchByTitle("bib", "les misérables");

        for (BookApi bookApi : bookApis) {
            System.out.println(bookApi.getTitle());
            System.out.println(bookApi.getIsbn());
        }
    }
}
