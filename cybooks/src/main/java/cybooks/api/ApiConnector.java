package cybooks.api;
import  cybooks.model.BookApi;

import static cybooks.utils.Config.API_BASE_URL;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * A class responsible for connecting to an external API to search for books.
 */
public class ApiConnector {

    /**
     * Default constructor for the ApiConnector class.
     */
    public ApiConnector(){

    }

    /**
     * Searches for books by author using the provided record type and author name.
     *
     * @param recordType The type of record to search for (e.g., "bib" or "aut").
     * @param author     The name of the author to search for.
     * @return A list of BookApi objects representing the books found.
     */
    public List<BookApi> searchByAuthor(String recordType, String author){
        String url = API_BASE_URL;
        String encodedQuery = "";
        String query = "";
        if(author.isEmpty()){
            System.out.println("ERROR : AUTHOR CANT BE EMPTY");
            return List.of();
        }
        if((recordType.isEmpty()) || (!recordType.equals("bib") && !recordType.equals("aut"))){
            System.out.println("ERROR : RECORD TYPE NOT VALID");
            return List.of();
        }
        query += "(" + recordType + ".author all \"" + author + "\")";
        encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        url += encodedQuery + "&recordSchema=dublincore&maximumRecords=500&startRecord=1";
        return this.get(url);
    }

    /**
     * Searches for books by ISBN using the provided record type and ISBN number.
     *
     * @param recordType The type of record to search for (e.g., "bib" or "aut").
     * @param isbn       The ISBN number of the book to search for.
     * @return A list of BookApi objects representing the books found.
     */
    public List<BookApi> searchByISBN(String recordType, String isbn){
        String url = API_BASE_URL;
        String encodedQuery = "";
        String query = "";
        if(isbn.isEmpty()){
            System.out.println("ERROR : ISBN CANT BE EMPTY");
            return List.of();
        }
        if((recordType.isEmpty()) || (!recordType.equals("bib") && !recordType.equals("aut"))){
            System.out.println("ERROR : RECORD TYPE NOT VALID");
            return List.of();
        }
        query += "(" + recordType + ".isbn adj \"" + isbn + "\")";
        encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        url += encodedQuery + "&recordSchema=dublincore&maximumRecords=500&startRecord=1";;
        return this.get(url);
    }

    /**
     * Searches for books by title using the provided record type and title.
     *
     * @param recordType The type of record to search for (e.g., "bib" or "aut").
     * @param title      The title of the book to search for.
     * @return A list of BookApi objects representing the books found.
     */
    public List<BookApi> searchByTitle(String recordType, String title){
        String url = API_BASE_URL;
        String encodedQuery = "";
        String query = "";

        if(title.isEmpty()){
            System.out.println("ERROR : TITLE CANT BE EMPTY");
            return List.of();
        }
        if((recordType.isEmpty()) || (!recordType.equals("bib") && !recordType.equals("aut"))){
            System.out.println("ERROR : RECORD TYPE NOT VALID");
            return List.of();
        }
        query += "(" + recordType + ".title all \"" + title + "\")";
        encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        url += encodedQuery + "&recordSchema=dublincore&maximumRecords=500&startRecord=1";
        return this.get(url);
    }

    /**
     * Executes an HTTP GET request to the specified API URL and parses the XML response.
     *
     * @param apiUrl The URL of the API to connect to.
     * @return A list of BookApi objects representing the books found.
     */
    private List<BookApi> get(String apiUrl){
        ParseXML parser = new ParseXML();
        try{
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder(new URI(apiUrl)).GET().build();
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return parser.readXML(res.body());
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
        }
        return List.of();
    }

}