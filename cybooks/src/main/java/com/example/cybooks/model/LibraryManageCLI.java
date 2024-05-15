package com.example.cybooks.model;

import java.util.Scanner;
import java.time.LocalDate;

/**
 * This class aims to give a command line interface to interact with the project.
 * It represents the step before the development of a graphic interface using JavaFX.
 */

public class LibraryManageCLI {
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
                    lendBook(scanner);
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
                    printAllLoans(scanner);
                    break;
                case 9:
                    printOverdueLoans(scanner);
                    break;
                case 10:
                    printUserProfile(scanner);
                    break;
                case 11:
                    printAllBookLoaned(scanner);
                    break;
                case 12:
                    printMostLoanedBooksLast30d(scanner);
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

    private void registerUser(Scanner scanner){
        System.out.println("Enter the user's name :");
        String name = scanner.nextLine();
        System.out.println("Enter the user's email :");
        String email = scanner.nextLine();
        System.out.println("Enter the user's address :");
        String address = scanner.nextLine();

        try{
            User user = new User(name, email, address);
            libraryManager.registerUser(user);
            System.out.println("User registered with ID : " + user.getUserID());
        } catch (IllegalArgumentException e){
            System.out.println("Error :" + e.getMessage());
        
        }
    }

    private void updateUser(Scanner scanner){
        System.out.println("Enter the user's ID :");
        int userID = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the user's new name :");
        String name = scanner.nextLine();
        System.out.println("Enter the user's new email :");
        String email = scanner.nextLine();
        System.out.println("Enter the user's new address :");
        String address = scanner.nextLine();

        try{
            User user = new User(userID, name, email, address);
            libraryManager.updateUser(user);
            System.out.println("User updated with ID : " + user.getUserID());
        } catch (IllegalArgumentException e){
            System.out.println("Error :" + e.getMessage());
        }
    }

    private void deleteUser(Scanner scanner){
        System.out.println("Enter the user's ID :");
        int userID = scanner.nextInt();
        scanner.nextLine();

        try{
            libraryManager.deleteUser(userID);
            System.out.println("User deleted with ID : " + userID);
        } catch (IllegalArgumentException e){
            System.out.println("Error :" + e.getMessage());
        }
        
    }

    private void lendBook(Scanner scanner){
        System.out.println("Enter the user's ID :");
        int userID = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the book's ID :");
        int bookID = scanner.nextInt();
        scanner.nextLine();

        try{
            libraryManager.lendBook(userID, bookID);
            System.out.println("Book lent to user with ID : " + userID);
        } catch (IllegalArgumentException e){
            System.out.println("Error :" + e.getMessage());
        }
    }

    private void returnBook(Scanner scanner){
        System.out.println("Enter the user's ID :");
        int userID = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the book's ID :");
        int bookID = scanner.nextInt();
        scanner.nextLine();

        try{
            libraryManager.returnBook(userID, bookID);
            System.out.println("Book returned by user with ID : " + userID);
        } catch (IllegalArgumentException e){
            System.out.println("Error :" + e.getMessage());
        }
    }

}
