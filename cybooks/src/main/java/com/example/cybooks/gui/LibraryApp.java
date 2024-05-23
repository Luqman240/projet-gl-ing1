package com.example.cybooks.gui;

import com.example.cybooks.manager.LibraryManager;
import com.example.cybooks.model.DataBase;
import com.example.cybooks.model.User;

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
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
        // Initialize the LibraryManager with your database connection
        DataBase db = new DataBase(); // Assurez-vous de créer et de configurer votre base de données
        libraryManager = new LibraryManager(db);
        db.startServer();

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
        Button btnPrintUserProfile = new Button("Print User Profile");

        btnAddUser.setOnAction(e -> showAddUserDialog());
        btnUpdateUser.setOnAction(e -> showUpdateUserDialog());
        btnDeleteUser.setOnAction(e -> showDeleteUserDialog());
        btnSearchUser.setOnAction(e -> showSearchUserDialog());
        btnPrintUserProfile.setOnAction(e -> showPrintUserProfileDialog());

        centerBox.getChildren().addAll(btnAddUser, btnUpdateUser, btnDeleteUser, btnSearchUser, btnPrintUserProfile);
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
        centerBox.getChildren().clear();
        Button btnById = new Button("By UserID");
        Button btnByEmail = new Button("By Email");

        btnById.setOnAction(e -> showByIdDialog());
        btnByEmail.setOnAction(e -> showByEmailDialog());
        centerBox.getChildren().addAll(btnById,btnByEmail);
    }

    private void showByIdDialog(){
        Stage dialog = new Stage();
        dialog.setTitle("By User ID");
    
        TextField IdField = new TextField();
        IdField.setPromptText("ID");
        Button submitButton = new Button("Submit");
    
        submitButton.setOnAction(e -> {
            User user = null;
            try{
                int userId = Integer.parseInt(IdField.getText());
                user = libraryManager.searchUser(userId);
            }
            catch (Exception ex){
                showAlert("Error", ex.getMessage());
            }
    
            if (user != null) {
                TextArea loansTextArea = new TextArea(user.toString());
                loansTextArea.setEditable(false);
                loansTextArea.setWrapText(true);
    
                Stage dialog2 = new Stage();
                dialog2.setTitle("View User");
    
                VBox dialogVBox = new VBox(10, loansTextArea);
                dialogVBox.setAlignment(Pos.CENTER);
                Scene dialogScene = new Scene(dialogVBox, 600, 400);
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });
    
        VBox dialogVBox = new VBox(10, IdField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showByEmailDialog(){
        Stage dialog = new Stage();
        dialog.setTitle("By Email");
    
        TextField EmailField = new TextField();
        EmailField.setPromptText("Email");
        Button submitButton = new Button("Submit");
    
        submitButton.setOnAction(e -> {
            User user = null;
            try{
                String userEmail = EmailField.getText();
                user = libraryManager.searchUser(userEmail);
            }
            catch (Exception ex){
                showAlert("Error", ex.getMessage());
            }
    
            if (user != null) {
                TextArea loansTextArea = new TextArea(user.toString());
                loansTextArea.setEditable(false);
                loansTextArea.setWrapText(true);
    
                Stage dialog2 = new Stage();
                dialog2.setTitle("View User");
    
                VBox dialogVBox = new VBox(10, loansTextArea);
                dialogVBox.setAlignment(Pos.CENTER);
                Scene dialogScene = new Scene(dialogVBox, 600, 400);
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });
    
        VBox dialogVBox = new VBox(10, EmailField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showPrintUserProfileDialog(){
        Stage dialog = new Stage();
        dialog.setTitle("Print User Profile");
    
        TextField IdField = new TextField();
        IdField.setPromptText("ID");
        Button submitButton = new Button("Submit");
    
        submitButton.setOnAction(e -> {
            User user = null;
            String userLoans = null;
            try{
                int userId = Integer.parseInt(IdField.getText());
                user = libraryManager.searchUser(userId);
                userLoans = libraryManager.getUserLoans(userId);
            }
            catch (Exception ex){
                showAlert("Error", ex.getMessage());
            }
    
            if (user != null && userLoans != null) {
                TextArea loansTextArea = new TextArea(user.toString() + "\nLoans:\n" + userLoans);
                loansTextArea.setEditable(false);
                loansTextArea.setWrapText(true);
    
                Stage dialog2 = new Stage();
                dialog2.setTitle("View User");
    
                VBox dialogVBox = new VBox(10, loansTextArea);
                dialogVBox.setAlignment(Pos.CENTER);
                Scene dialogScene = new Scene(dialogVBox, 600, 400);
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });
    
        VBox dialogVBox = new VBox(10, IdField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showBookOperations() {
        centerBox.getChildren().clear();

        Button btnSearchBook = new Button("Search Book");
        Button btnReturnBook = new Button("Return Book");
        Button btnPrintLoanBook = new Button("Print Loan Book");
        
        btnSearchBook.setOnAction(e -> showSearchBookDialog());
        btnReturnBook.setOnAction(e -> showReturnBookDialog());
        btnPrintLoanBook.setOnAction(e -> showPrintLoanBookDialog());

        centerBox.getChildren().addAll(btnSearchBook, btnReturnBook,btnPrintLoanBook);
    }

    private void showSearchBookDialog() {
        centerBox.getChildren().clear();
        Button btnByISBN = new Button("By ISBN");
        Button btnByTitle = new Button("By Title");
        Button btnByAuthor = new Button("By Author");

        btnByISBN.setOnAction(e -> showByISBNDialog());
        btnByTitle.setOnAction(e -> showByTitleDialog());
        btnByAuthor.setOnAction(e -> showByAuthorDialog());
        centerBox.getChildren().addAll(btnByISBN, btnByTitle, btnByAuthor);
    }

    private void showByISBNDialog(){
        Stage dialog = new Stage();
        dialog.setTitle("By ISBN");
    
        TextField ISBNField = new TextField();
        ISBNField.setPromptText("ISBN");
        Button submitButton = new Button("Submit");
    
        submitButton.setOnAction(e -> {
            String book = null;
            try{
                String isbn = ISBNField.getText();
                book = libraryManager.searchBook(isbn, "isbn");
            }
            catch (Exception ex){
                showAlert("Error", ex.getMessage());
            }
    
            if (book != null) {
                TextArea loansTextArea = new TextArea(book);
                loansTextArea.setEditable(false);
                loansTextArea.setWrapText(true);
    
                Stage dialog2 = new Stage();
                dialog2.setTitle("View Book");
    
                VBox dialogVBox = new VBox(10, loansTextArea);
                dialogVBox.setAlignment(Pos.CENTER);
                Scene dialogScene = new Scene(dialogVBox, 600, 400);
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });
    
        VBox dialogVBox = new VBox(10, ISBNField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showByTitleDialog(){
        Stage dialog = new Stage();
        dialog.setTitle("By Title");
    
        TextField TitleField = new TextField();
        TitleField.setPromptText("Title");
        Button submitButton = new Button("Submit");
    
        submitButton.setOnAction(e -> {
            String book = null;
            try{
                String title = TitleField.getText();
                book = libraryManager.searchBook(title, "title");
            }
            catch (Exception ex){
                showAlert("Error", ex.getMessage());
            }
    
            if (book != null) {
                TextArea loansTextArea = new TextArea(book);
                loansTextArea.setEditable(false);
                loansTextArea.setWrapText(true);
    
                Stage dialog2 = new Stage();
                dialog2.setTitle("View Book");
    
                VBox dialogVBox = new VBox(10, loansTextArea);
                dialogVBox.setAlignment(Pos.CENTER);
                Scene dialogScene = new Scene(dialogVBox, 600, 400);
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });
    
        VBox dialogVBox = new VBox(10, TitleField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showByAuthorDialog(){
        Stage dialog = new Stage();
        dialog.setTitle("By Author");
    
        TextField AuthorField = new TextField();
        AuthorField.setPromptText("Author");
        Button submitButton = new Button("Submit");
    
        submitButton.setOnAction(e -> {
            String book = null;
            try{
                String author = AuthorField.getText();
                book = libraryManager.searchBook(author, "author");
            }
            catch (Exception ex){
                showAlert("Error", ex.getMessage());
            }
    
            if (book != null) {
                TextArea loansTextArea = new TextArea(book);
                loansTextArea.setEditable(false);
                loansTextArea.setWrapText(true);
    
                Stage dialog2 = new Stage();
                dialog2.setTitle("View Book");
    
                VBox dialogVBox = new VBox(10, loansTextArea);
                dialogVBox.setAlignment(Pos.CENTER);
                Scene dialogScene = new Scene(dialogVBox, 600, 400);
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });
    
        VBox dialogVBox = new VBox(10, AuthorField, submitButton);
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

    private void showPrintLoanBookDialog() {
        String books = libraryManager.viewLoans(true,false); // Modifier les paramètres selon vos besoins
        TextArea booksTextArea = new TextArea(books);
        booksTextArea.setEditable(false);
        booksTextArea.setWrapText(true);

        Stage dialog = new Stage();
        dialog.setTitle("View Books");

        VBox dialogVBox = new VBox(10, booksTextArea);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 600, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }
    private void showLoanOperations() {
        centerBox.getChildren().clear();

        Button btnLoanBook = new Button("Loan Book");
        Button btnPrintLoan = new Button("Print all Loan");
        Button btnReturnLoan = new Button("PrintOverdue Loan");
        Button btnRenewLoan = new Button("printMostLoanedBooksLast30d");

        btnLoanBook.setOnAction(e -> showLoanBookDialog());
        btnPrintLoan.setOnAction(e -> showPrintLoanDialog());
        btnReturnLoan.setOnAction(e -> showReturnLoanDialog());
        btnRenewLoan.setOnAction(e -> showRenewLoanDialog());

        centerBox.getChildren().addAll(btnLoanBook,btnPrintLoan, btnReturnLoan, btnRenewLoan);
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

