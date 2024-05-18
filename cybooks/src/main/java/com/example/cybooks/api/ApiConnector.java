package com.example.cybooks.api;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import static com.example.cybooks.utils.Config.API_BASE_URL;

public class ApiConnector {

    public ApiConnector(){

    }

    public void searchByAuthor(String recordType, String author){
        String url = API_BASE_URL;
        String encodedQuery = "";
        String query = "";
        if(author.isEmpty()){
            System.out.println("ERROR : AUTHOR CANT BE EMPTY");
            return;
        }
        if((recordType.isEmpty()) || (!recordType.equals("bib") && !recordType.equals("aut"))){
            System.out.println("ERROR : RECORD TYPE NOT VALID");
            return;
        }
        query += "(" + recordType + ".author all \"" + author + "\")";
        encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        url += encodedQuery + "&recordSchema=dublincore";
        this.get(url);
    }

    public void searchByISBN(String recordType, String isbn){
        String url = API_BASE_URL;
        String encodedQuery = "";
        String query = "";
        if(isbn.isEmpty()){
            System.out.println("ERROR : ISBN CANT BE EMPTY");
            return;
        }
        if((recordType.isEmpty()) || (!recordType.equals("bib") && !recordType.equals("aut"))){
            System.out.println("ERROR : RECORD TYPE NOT VALID");
            return;
        }
        query += "(" + recordType + ".isbn adj \"" + isbn + "\")";
        encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        url += encodedQuery;
        this.get(url);
    }

    public void searchByTitle(String recordType, String title){
        String url = API_BASE_URL;
        String encodedQuery = "";
        String query = "";

        if(title.isEmpty()){
            System.out.println("ERROR : TITLE CANT BE EMPTY");
            return;
        }
        if((recordType.isEmpty()) || (!recordType.equals("bib") && !recordType.equals("aut"))){
            System.out.println("ERROR : RECORD TYPE NOT VALID");
            return;
        }
        query += "(" + recordType + ".title all \"" + title + "\")";
        encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        url += encodedQuery + "&recordSchema=dublincore";
        this.get(url);
    }


    private void get(String apiUrl){
        try{
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder(new URI(apiUrl)).GET().build();
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println(res.body());
            //System.out.println(res.body());
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
        }
    }
}