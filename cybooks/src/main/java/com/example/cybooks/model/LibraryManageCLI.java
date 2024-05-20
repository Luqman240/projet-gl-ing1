package com.example.cybooks.model;


import com.example.cybooks.exception.*;


import java.util.Scanner;
import java.time.LocalDate;

/**
 * This class aims to give a command line interface to interact with the project.
 * It represents the step before the development of a graphic interface using JavaFX.
 */

public class LibraryManageCLI  {
    private DataBase db;
    private LibraryManager libraryManager;

    public LibraryManageCLI() {
        db = new DataBase();
        db.startServer();
        libraryManager = new LibraryManager(db);
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while(!exit){
            printMenu();
            System.out.println("Enter your choice :");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice){
                case 1 :
                    registerUser(scanner);
                    break;
                case 2 :
                    updateUser(scanner);
                    break;
                case 3 :
                    deleteUser(scanner);
                    break;
                case 4 :
                    loanBook(scanner);
                    break;
                case 5 :
                    returnBook(scanner);
                    break;
                case 6:
                    searchUser(scanner);
                    break;
                case 7:
                    searchBook(scanner);
                    break;
                case 8:
                    printAllLoans();
                    break;
                case 9:
                    printOverdueLoans(scanner);
                    break;
                case 10:
                    printUserProfile(scanner);
                    break;
                case 11:
                    printAllBookLoaned();
                    break;
                case 12:
                    printMostLoanedBooksLast30d();
                    break;
                case 13:
                    exit = true;
                    db.stopServer();
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
                }
            }
            scanner.close();
        }

    private void printMenu(){
        System.out.println("\nCY-Books Library Manager :");
        System.out.println("1. Register a new user.");
        System.out.println("2. Update a user's information.");
        System.out.println("3. Delete a user.");
        System.out.println("4. Lend a book.");
        System.out.println("5. Return a book.");
        System.out.println("6. Search for a User");
        System.out.println("7. Search for a book information.");
        System.out.println("8. Print all loans.");
        System.out.println("9. Print overdue loans.");
        System.out.println("10. Print a user's profile.");
        System.out.println("11. Print all books loaned.");
        System.out.println("12. Print most loaned books on the last 30 days.");
        System.out.println("13. Exit.");
    }

    private void registerUser(Scanner scanner) { //done
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        try {
            libraryManager.registerUser(name, email, address);
            System.out.println("User registered successfully.");
        } catch (InvalidEmailFormatException | EmailAlreadyExistsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateUser(Scanner scanner){ //done
        System.out.print("Enter user ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (!libraryManager.userExists(userID)) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("Enter new name (leave empty to keep current): ");
        String name = scanner.nextLine();

        System.out.print("Enter new email (leave empty to keep current): ");
        String email = scanner.nextLine();

        System.out.print("Enter new address (leave empty to keep current): ");
        String address = scanner.nextLine();

        try {
            libraryManager.updateUser(userID, name, email, address);
            System.out.println("User updated successfully.");
        } catch (UserNotFoundException | InvalidEmailFormatException | EmailAlreadyExistsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteUser(Scanner scanner){ // done + we need to add loan verification
        System.out.print("Enter user ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        try {
            libraryManager.deleteUser(userID);
            System.out.println("User deleted successfully.");
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loanBook(Scanner scanner){ //Done
        System.out.print("Enter user ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        if (!libraryManager.userExists(userID)) {
            System.out.println("User not found.");
            return;
        }
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();

        if (!libraryManager.isbnExistsInBNF(isbn)) {
            System.out.println("Book not found.");
            return;
        }

        try {
            libraryManager.loanBook(userID, isbn);
            System.out.println("Loan added successfully.");
        } catch (UserNotFoundException | NoCopyAvailableException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void returnBook(Scanner scanner){ //Done
        System.out.print("Enter user ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (!libraryManager.userExists(userID)) {
            System.out.println("User not found.");
            return;
        }
        
        System.out.print("Enter ISBN of the loaned book :");
        String isbn = scanner.nextLine();

        try {
            libraryManager.returnBook(userID, isbn);
            System.out.println("Book returned successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchUser(Scanner scanner) {
        System.out.print("How do you want to search for the user ?\n1. By ID\n2. By email\nEnter your choice :");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                searchUserByID(scanner);
                break;
            case 2:
                searchUserByEmail(scanner);
                break;
            default:
                System.out.println("Invalid choice, please try again.");
        }
    }

    private void searchUserByID(Scanner scanner) { //Done
        System.out.print("Enter user ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        try {
            User user = libraryManager.searchUser(userID);
            System.out.println(user.toString());
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchUserByEmail(Scanner scanner) { //Done
        System.out.print("Enter user email: ");
        String email = scanner.nextLine();

        try {
            User user = libraryManager.searchUser(email);
            System.out.println(user.toString());
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchBook(Scanner scanner) { //Pending BNF API methods
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();

        try {
            String result = libraryManager.searchBook(isbn);
            System.out.println(result);
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printAllLoans() { //Done
        String result = libraryManager.viewLoans(false, false);
        System.out.println(result);
    }

    private void printOverdueLoans(Scanner scanner) { //Done
        System.out.print("Today's date is : ");
        System.out.println(LocalDate.now());

        try {
            String result = libraryManager.viewLoans(true, true);
            System.out.println(result);
            System.out.println("End of overdue loans.");
        } catch (Exception e) {
            System.out.println("Error: Invalid date format.");
        }
    }

    private void printUserProfile(Scanner scanner) {
        System.out.print("Enter user ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        try {
            User user = libraryManager.searchUser(userID);
            String userLoans = libraryManager.getUserLoans(userID);

            System.out.println("\nHere are the user's informations :");
            System.out.println(user.toString() + "\n");
            System.out.println("Here are the user's loans :");
            System.out.println(userLoans);

        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printAllBookLoaned() {

        System.out.println("Here are all the books currently loaned :");
        String result = libraryManager.viewLoans(true, false);
        System.out.println(result);
    }

    private void printMostLoanedBooksLast30d(){
        System.out.println("Here are the 5 most loaned books in the last 30 days :");
        String result = libraryManager.mostLoanedBooksLast30d();
        System.out.println(result);
    }


    // Main method to run the CLI
    public static void main(String[] args) {
        LibraryManageCLI cli = new LibraryManageCLI();
        cli.run();
    }

}
