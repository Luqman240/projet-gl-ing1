package com.example.cybooks.model;

public class BNFApiConnector {

    private String apiURL;

    public BNFApiConnector(String apiURL) {
        this.apiURL = apiURL;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public fetchBookDetails(String isbn) {
        // Code to fetch book details from the BNF API
        return "Book details for ISBN " + isbn;
    }
}
