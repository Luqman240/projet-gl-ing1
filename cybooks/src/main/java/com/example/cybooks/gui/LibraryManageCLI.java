package com.example.cybooks.gui;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.example.cybooks.exception.BookNotFoundException;
import com.example.cybooks.exception.EmailAlreadyExistsException;
import com.example.cybooks.exception.InvalidEmailFormatException;
import com.example.cybooks.exception.NoCopyAvailableException;
import com.example.cybooks.exception.UserHasLoansException;
import com.example.cybooks.exception.UserNotFoundException;
import com.example.cybooks.manager.LibraryManager;
import com.example.cybooks.model.DataBase;
import com.example.cybooks.model.User;

import java.time.LocalDate;

/**
 * This class provides a command line interface to interact with the library management system.
 * It serves as a precursor to the development of a graphical interface using JavaFX.
 */
public class LibraryManageCLI {
    private DataBase db;
    private LibraryManager libraryManager;

    /**
     * Constructor to initialize the database and library manager.
     */
    public LibraryManageCLI() {
        db = new DataBase();
        db.startServer();
        libraryManager = new LibraryManager(db);
    }

    /**
     * Main method to run the command line interface.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            printMenu();
            System.out.println("Enter your choice:");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    registerUser(scanner); //
                    break;
                case 2:
                    updateUser(scanner);//
                    break;
                case 3:
                    deleteUser(scanner);//
                    break;
                case 4:
                    loanBook(scanner); //
                    break;
                case 5:
                    returnBook(scanner);//
                    break;
                case 6:
                    searchUser(scanner);//
                    break;
                case 7:
                    searchBook(scanner);//
                    break;
                case 8:
                    printAllLoans(); //
                    break;
                case 9:
                    printOverdueLoans(scanner);//
                    break;
                case 10:
                    printUserProfile(scanner);//
                    break;
                case 11:
                    printAllBookLoaned();
                    break;
                case 12:
                    printMostLoanedBooksLast30d(); //
                    break;
                case 13:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
        scanner.close();
    }

    /**
     * Prints the menu options to the console.
     */
    private void printMenu() {
        System.out.println("\nCY-Books Library Manager:");
        System.out.println("1. Register a new user.");
        System.out.println("2. Update a user's information.");
        System.out.println("3. Delete a user.");
        System.out.println("4. Lend a book.");
        System.out.println("5. Return a book.");
        System.out.println("6. Search for a User");
        System.out.println("7. Search for a book information, with criteria.");
        System.out.println("8. Print all loans.");
        System.out.println("9. Print overdue loans.");
        System.out.println("10. Print a user's profile.");
        System.out.println("11. Print all books loaned.");
        System.out.println("12. Print most loaned books in the last 30 days.");
        System.out.println("13. Exit.");
    }

    /**
     * Registers a new user.
     *
     * @param scanner the Scanner object for input
     */
    private void registerUser(Scanner scanner) {
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

    /**
     * Updates a user's information.
     *
     * @param scanner the Scanner object for input
     */
    private void updateUser(Scanner scanner) {
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

    /**
     * Deletes a user.
     *
     * @param scanner the Scanner object for input
     */
    private void deleteUser(Scanner scanner) {
        System.out.print("Enter user ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        try {
            libraryManager.deleteUser(userID);
            System.out.println("User deleted successfully.");
        } catch (UserNotFoundException | UserHasLoansException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Lends a book to a user.
     *
     * @param scanner the Scanner object for input
     */
    private void loanBook(Scanner scanner) {
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

    /**
     * Returns a book for a user.
     *
     * @param scanner the Scanner object for input
     */
    private void returnBook(Scanner scanner) {
        System.out.print("Enter user ID: ");
        int userID = 0;
        try {
            userID = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. User ID must be an integer.");
            scanner.nextLine();  // Consume the invalid input
            return;
        }

        if (!libraryManager.userExists(userID)) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("Enter ISBN of the loaned book: ");
        String isbn = scanner.nextLine();
        System.out.println(isbn);  // Print the entered ISBN

        try {
            libraryManager.returnBook(userID, isbn);
            System.out.println("Book returned successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Searches for a user by ID or email.
     *
     * @param scanner the Scanner object for input
     */
    private void searchUser(Scanner scanner) {
        System.out.print("How do you want to search for the user?\n1. By ID\n2. By email\nEnter your choice: ");
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

    /**
     * Searches for a user by ID.
     *
     * @param scanner the Scanner object for input
     */
    private void searchUserByID(Scanner scanner) {
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

    /**
     * Searches for a user by email.
     *
     * @param scanner the Scanner object for input
     */
    private void searchUserByEmail(Scanner scanner) {
        System.out.print("Enter user email: ");
        String email = scanner.nextLine();

        try {
            User user = libraryManager.searchUser(email);
            System.out.println(user.toString());
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Searches for a book by ISBN, title, or author.
     *
     * @param scanner the Scanner object for input
     */
    private void searchBook(Scanner scanner) {
        System.out.println("Choose search type:");
        System.out.println("1. ISBN");
        System.out.println("2. Title");
        System.out.println("3. Author");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume the newline left by nextInt

        String searchType;
        switch (choice) {
            case 1:
                searchType = "isbn";
                System.out.print("Enter ISBN: ");
                break;
            case 2:
                searchType = "title";
                System.out.print("Enter Title (Don't put emphasis): ");
                break;
            case 3:
                searchType = "author";
                System.out.print("Enter Author: ");
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        String searchTerm = scanner.nextLine();

        try {
            String result = libraryManager.searchBook(searchTerm, searchType);
            System.out.println(result);
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prints all loans.
     */
    private void printAllLoans() {
        String result = libraryManager.viewLoans(false, false);
        System.out.println(result);
    }

    /**
     * Prints overdue loans.
     *
     * @param scanner the Scanner object for input
     */
    private void printOverdueLoans(Scanner scanner) {
        System.out.print("Today's date is: ");
        System.out.println(LocalDate.now());

        try {
            String result = libraryManager.viewLoans(true, true);
            System.out.println(result);
            System.out.println("End of overdue loans.");
        } catch (Exception e) {
            System.out.println("Error: Invalid date format.");
        }
    }

    /**
     * Prints a user's profile including their loans.
     *
     * @param scanner the Scanner object for input
     */
    private void printUserProfile(Scanner scanner) {
        System.out.print("Enter user ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        try {
            User user = libraryManager.searchUser(userID);
            String userLoans = libraryManager.getUserLoans(userID);

            System.out.println("\nHere are the user's informations:");
            System.out.println(user.toString() + "\n");
            System.out.println("Here are the user's loans:");
            System.out.println(userLoans);

        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prints all books that are currently loaned.
     */
    private void printAllBookLoaned() {
        System.out.println("Here are all the books currently loaned:");
        String result = libraryManager.viewLoans(true, false);
        System.out.println(result);
    }

    /**
     * Prints the most loaned books in the last 30 days.
     */
    private void printMostLoanedBooksLast30d() {
        System.out.println("Here are the 5 most loaned books in the last 30 days:");
        String result = libraryManager.mostLoanedBooksLast30d();
        System.out.println(result);
    }

    /**
     * Main method to run the CLI.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LibraryManageCLI cli = new LibraryManageCLI();
        cli.run();
    }
}
