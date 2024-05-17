package com.example.cybooks.model;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The LibraryManager class represents a system for managing library operations.
 * It facilitates tasks such as registering and unregistering users, checking out and returning books,
 * searching for books via an external API, and managing loan records.
 * 
 * This class maintains lists of users and loans, and it interacts with a BNFApiConnector to fetch book details
 * and handle book checkout operations. It also keeps track of the total number of copies per book allowed in the library.
 * 
 * Users can be registered and unregistered from the library, and books can be checked out and returned.
 * The class also provides methods for searching books based on various criteria and retrieving lists of overdue loans.
 */
public class LibraryManager {
    /*
     * le constructeur doit prendre ApiConnector,un objet database,totalCopiesPerBook
     */
    private List<User> users = new ArrayList<>();
    private List<Loan> loans = new ArrayList<>();
    private BNFApiConnector apiConnector;
    private int totalCopiesPerBook = 3; // Example: 3 copies of each book

    // Constructor
    public LibraryManager(BNFApiConnector apiConnector) {
        this.apiConnector = apiConnector;
    }

    /**
     * Registers a new user to the library.
     * 
     * @param user The user to be registered.
     */
    public void registerUser(User user) {
        users.add(user);
    }

    /**
     * Unregisters a user from the library.
     * 
     * @param userId The ID of the user to be unregistered.
     */
    public void unregisterUser(int userId) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID() == userId) {
                users.remove(i);
                System.out.println("The user with ID " + userId + " has been removed from the library.");
                return;
            }
    }

    /**
    * Updates the information of a user in the library.
    * 
    * @param userId   The ID of the user whose information is to be updated.
    * @param newName  The new name to set for the user. If null or empty, the name remains unchanged.
    * @param newEmail The new email to set for the user. If null, empty, or not in valid email format,
    *                 the email remains unchanged.
    */
    public void updateUser(int userId, String newName, String newEmail, String newAddress) {
        for (User user : users) {
            if (user.getUserID() == userId) {
                if (newName != null && !newName.isEmpty()) {
                    user.setName(newName);
                }
                if (newEmail != null && !newEmail.isEmpty() && isValidEmail(newEmail)) {
                    user.setEmail(newEmail);
                }
                if (newAddress != null && !newAddress.isEmpty()) {
                    user.setAddress(newAddress);
                }
                System.out.println("User information updated successfully.");
                return;
            }
        }
        System.out.println("User with ID " + userId + " not found.");
    }

    /**
    * Checks if the given email string is in a valid email format.
    * 
    * @param email The email string to be validated.
    * @return True if the email string is in a valid email format, false otherwise.
    */
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
    * Finds a user in the library by their ID.
    * 
    * @param userId The ID of the user to find.
    * @return The user object if found, or null if not found.
    */
    public User findUserById(int userId) {
        for (User user : users) {
            if (user.getUserID() == userId) {
                return user;
            }
        }
        return null; // User not found
    }
 
    /**
     * Searches for books based on the provided criteria using the BNF API.
     * 
     * @param criteria The search criteria.
     * @return A list of book details matching the search criteria.
     */
    public List<Map<String, String>> searchBooks(Map<String, String> criteria) {
        // Simulated search, would be replaced by actual API calls and filtering logic
        List<Map<String, String>> results = new ArrayList<>();

        for (Map<String, String> book : books) { 
            boolean match = true;

            for (Map.Entry<String, String> entry : criteria.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                switch (key) {
                    case "identifier":
                        if (!book.containsKey("identifier") || !book.get("identifier").equals(value)) {
                            match = false;
                        }
                        break;
                    case "title":
                        if (!book.containsKey("title") || !book.get("title").toLowerCase().contains(value.toLowerCase())) {
                            match = false;
                        }
                        break;
                    case "creator":
                        if (!book.containsKey("author") || !book.get("author").toLowerCase().contains(value.toLowerCase())) {
                            match = false;
                        }
                        break;
                    case "date":
                        if (!book.containsKey("date") || !book.get("date").equals(value)) {
                            match = false;
                        }
                        break;
                    case "description":
                        if (!book.containsKey("description") || !book.get("description").toLowerCase().contains(value.toLowerCase())) {
                            match = false;
                        }
                        break;
                    case "format":
                        if (!book.containsKey("format") || !book.get("format").toLowerCase().contains(value.toLowerCase())) {
                            match = false;
                        }
                        break;
                    case "language":
                        if (!book.containsKey("language") || !book.get("language").toLowerCase().contains(value.toLowerCase())) {
                            match = false;
                        }
                        break;
                    case "publisher":
                        if (!book.containsKey("publisher") || !book.get("publisher").toLowerCase().contains(value.toLowerCase())) {
                            match = false;
                        }
                        break;
                }

                if (!match) {
                    break; // No need to check further if one criterion doesn't match
                }
            }

        if (match) {
            results.add(book);
        }
    }

    return results;
}

    /**
     * Checks out a book for a user.
     * 
     * @param userId The ID of the user who is checking out the book.
     * @param isbn   The ISBN of the book to be checked out.
     */
    public void checkoutBook(int userID, String isbn, int numberOfDay) {
        long count = loans.stream().filter(loan -> loan.getBookID().equals(isbn)).count();
        if (count < totalCopiesPerBook) {
            // Fetch book details from BNF API
            Map<String, String> bookDetails = apiConnector.fetchBookDetails(isbn);
            String bookId = bookDetails.get("isbn"); // Simplified

            // Create a new loan
            Loan loan = new Loan(userID, bookId,numberOfDay);
            loans.add(loan);
        } else {
            System.out.println("No copies available for book " + isbn);
        }
    }

    /**
     * Returns a book that was checked out by a user.
     * 
     * @param loanId The ID of the loan associated with the book being returned.
     */
    public void returnBook(String loanId) {
        for (Loan loan : loans) {
            if (loan.getLoanId().equals(loanId) && loan.getReturnDate() == null) {
                loan.setReturnDate(new Date());
                loans.remove(loan);
                System.out.println("The book has been returned.");
                break;
            }
        }
    } 


    /**
     * Retrieves a list of overdue loans.
     * 
     * @return A list of overdue loans.
     */
    public List<Loan> getOverdueLoans() {
        return loans.stream()
            .filter(loan -> loan.getReturnDate() == null && loan.getDueDate().before(new Date()))
            .collect(Collectors.toList());
    }

    /**
     * Getter for the list of users in the library.
     * 
     * @return The list of users.
     */
    public List<User> getUsers() { 
        return users; 
    }

    /**
     * Setter for the list of users in the library.
     * 
     * @param users The list of users to set.
     */
    public void setUsers(List<User> users) { 
        this.users = users; 
    }

    /**
     * Getter for the list of loans in the library.
     * 
     * @return The list of loans.
     */
    public List<Loan> getLoans() { 
        return loans;
    }

    /**
     * Setter for the list of loans in the library.
     * 
     * @param loans The list of loans to set.
     */
    public void setLoans(List<Loan> loans) { 
        this.loans = loans; 
    }

    /**
     * Getter for the BNFApiConnector used by the library.
     * 
     * @return The BNFApiConnector.
     */
    public BNFApiConnector getApiConnector() { 
        return apiConnector; 
    }

    /**
     * Setter for the BNFApiConnector used by the library.
     * 
     * @param apiConnector The BNFApiConnector to set.
     */
    public void setApiConnector(BNFApiConnector apiConnector) { 
        this.apiConnector = apiConnector; 
    }

    /**
     * Getter for the total number of copies allowed per book in the library.
     * 
     * @return The total number of copies per book.
     */
    public int getTotalCopiesPerBook() { 
        return totalCopiesPerBook; 
    }

    /**
     * Setter for the total number of copies allowed per book in the library.
     * 
     * @param totalCopiesPerBook The total number of copies per book to set.
     */
    public void setTotalCopiesPerBook(int totalCopiesPerBook) { 
        this.totalCopiesPerBook = totalCopiesPerBook; 
    }
}