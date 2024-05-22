package com.exemple.cybooks.gui;

import com.exemple.cybooks.manager.LibraryManager;
import com.exemple.cybooks.model.DataBase;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibraryApp extends Application {
    private LibraryManager libraryManager;
    private VBox centerBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize the LibraryManager with your database connection
        DataBase db = new DataBase(); // Assurez-vous de créer et de configurer votre base de données
        libraryManager = new LibraryManager(db);

        primaryStage.setTitle("Library Management System");

        // Create main buttons
        Button btnUsers = new Button("Users");
        Button btnBooks = new Button("Books");
        Button btnLoans = new Button("Loans");

        // HBox to hold main buttons
        HBox mainButtonsBox = new HBox(10, btnUsers, btnBooks, btnLoans);
        mainButtonsBox.setAlignment(Pos.CENTER);

        // Footer label
        Label footerLabel = new Label("LibraryApp © 2024");

        // VBox to hold footer
        VBox footerBox = new VBox(footerLabel);
        footerBox.setAlignment(Pos.CENTER);

        // VBox to display operation buttons
        centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);

        // BorderPane to arrange the layout
        BorderPane root = new BorderPane();
        root.setTop(mainButtonsBox);
        root.setCenter(centerBox);
        root.setBottom(footerBox);

        // Set actions for main buttons
        btnUsers.setOnAction(e -> showUserOperations());
        btnBooks.setOnAction(e -> showBookOperations());
        btnLoans.setOnAction(e -> showLoanOperations());

        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    private void showUserOperations() {
        centerBox.getChildren().clear();

        Button btnAddUser = new Button("Add User");
        Button btnUpdateUser = new Button("Update User");
        Button btnDeleteUser = new Button("Delete User");
        Button btnSearchUser = new Button("Search User");
        //Button btnPrintUserProfile = new Button("Print User Profile");

        btnAddUser.setOnAction(e -> showAddUserDialog());
        btnUpdateUser.setOnAction(e -> showUpdateUserDialog());
        btnDeleteUser.setOnAction(e -> showDeleteUserDialog());
        btnSearchUser.setOnAction(e -> showSearchUserDialog());
        //btnPrintUserProfile.setOnAction(e -> showPrintUserProfileDialog());

        centerBox.getChildren().addAll(btnAddUser, btnUpdateUser, btnDeleteUser);
    }

    private void showAddUserDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add User");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String address = addressField.getText();

            try {
                libraryManager.registerUser(name, email, address);
                showAlert("Success", "User registered successfully!");
                dialog.close();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        VBox dialogVBox = new VBox(10, nameField, emailField, addressField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showUpdateUserDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Update User");

        TextField idField = new TextField();
        idField.setPromptText("User ID");
        TextField nameField = new TextField();
        nameField.setPromptText("New Name");
        TextField emailField = new TextField();
        emailField.setPromptText("New Email");
        TextField addressField = new TextField();
        addressField.setPromptText("New Address");
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            int userId = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            String email = emailField.getText();
            String address = addressField.getText();

            try {
                libraryManager.updateUser(userId, name, email, address);
                showAlert("Success", "User updated successfully!");
                dialog.close();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        VBox dialogVBox = new VBox(10, idField, nameField, emailField, addressField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 250);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showDeleteUserDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Delete User");

        TextField idField = new TextField();
        idField.setPromptText("User ID");
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            int userId = Integer.parseInt(idField.getText());

            try {
                libraryManager.deleteUser(userId);
                showAlert("Success", "User deleted successfully!");
                dialog.close();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        VBox dialogVBox = new VBox(10, idField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 150);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showSearchUserDialog(){

        Button btnById = new Button("By ID");
        //Button btnByEmail = new Button("By Email");

        btnById.setOnAction(e -> showByIdDialog());
        //btnByEmail.setOnAction(e -> showByEmailDialog());
    }

    private void showByIdDialog(){
        Stage dialog = new Stage();
        dialog.setTitle("By User ID");

        TextField IdField = new TextField();
        IdField.setPromptText("ID");
    }

    private void showBookOperations() {
        centerBox.getChildren().clear();

        Button btnAddBook = new Button("Add Book");
        Button btnLoanBook = new Button("Loan Book");
        Button btnReturnBook = new Button("Return Book");

        btnAddBook.setOnAction(e -> showAddBookDialog());
        btnLoanBook.setOnAction(e -> showLoanBookDialog());
        btnReturnBook.setOnAction(e -> showReturnBookDialog());

        centerBox.getChildren().addAll(btnAddBook, btnLoanBook, btnReturnBook);
    }

    private void showAddBookDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Book");

        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        TextField copiesField = new TextField();
        copiesField.setPromptText("Copies Available");
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            String isbn = isbnField.getText();
            int copies = Integer.parseInt(copiesField.getText());

            libraryManager.addBook(isbn, copies);
            showAlert("Success", "Book added successfully!");
            dialog.close();
        });

        VBox dialogVBox = new VBox(10, isbnField, copiesField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showLoanBookDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Loan Book");

        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID");
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            int userId = Integer.parseInt(userIdField.getText());
            String isbn = isbnField.getText();

            try {
                libraryManager.loanBook(userId, isbn);
                showAlert("Success", "Book loaned successfully!");
                dialog.close();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        VBox dialogVBox = new VBox(10, userIdField, isbnField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showReturnBookDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Return Book");

        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID");
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            int userId = Integer.parseInt(userIdField.getText());
            String isbn = isbnField.getText();

            try {
                libraryManager.returnBook(userId, isbn);
                showAlert("Success", "Book returned successfully!");
                dialog.close();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        VBox dialogVBox = new VBox(10, userIdField, isbnField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showLoanOperations() {
        centerBox.getChildren().clear();

        Button btnPrintLoan = new Button("Print all Loan");
        Button btnReturnLoan = new Button("PrintOverdue Loan");
        Button btnRenewLoan = new Button("printMostLoanedBooksLast30d");

        btnPrintLoan.setOnAction(e -> showPrintLoanDialog());
        btnReturnLoan.setOnAction(e -> showReturnLoanDialog());
        btnRenewLoan.setOnAction(e -> showRenewLoanDialog());

        centerBox.getChildren().addAll(btnPrintLoan, btnReturnLoan, btnRenewLoan);
    }

    private void showPrintLoanDialog() {
        String loans = libraryManager.viewLoans(false, false); // Modifier les paramètres selon vos besoins
        TextArea loansTextArea = new TextArea(loans);
        loansTextArea.setEditable(false);
        loansTextArea.setWrapText(true);

        Stage dialog = new Stage();
        dialog.setTitle("View Loans");

        VBox dialogVBox = new VBox(10, loansTextArea);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 600, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showReturnLoanDialog() {
        String loans = libraryManager.viewLoans(true, true); // Modifier les paramètres selon vos besoins
        TextArea loansTextAreaa = new TextArea(loans);
        loansTextAreaa.setEditable(false);
        loansTextAreaa.setWrapText(true);

        Stage dialog = new Stage();
        dialog.setTitle("View Loans");

        VBox dialogVBox = new VBox(10, loansTextAreaa);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 600, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showRenewLoanDialog() {
        String loans = libraryManager.mostLoanedBooksLast30d(); // Modifier les paramètres selon vos besoins
        TextArea loansTextAreaa = new TextArea(loans);
        loansTextAreaa.setEditable(false);
        loansTextAreaa.setWrapText(true);

        Stage dialog = new Stage();
        dialog.setTitle("View Loans");

        VBox dialogVBox = new VBox(10, loansTextAreaa);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 600, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

