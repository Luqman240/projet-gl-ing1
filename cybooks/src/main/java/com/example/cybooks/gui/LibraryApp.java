package com.example.cybooks.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.cybooks.manager.LibraryManager;
import com.example.cybooks.model.DataBase;
import com.example.cybooks.model.User;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
* The main class for the Library Management System application.
* This class is responsible for initializing the application and setting up the user interface.
*/
public class LibraryApp extends Application {
    private LibraryManager libraryManager;
    private VBox centerBox;

    /**
    * The main method that launches the application.
    * @param args command line arguments
    */
    public static void main(String[] args) {
        launch(args);
    }

    /**
    * The start method is called after the init method has returned, and after the system is ready for the application to begin running.
    * @param primaryStage the primary stage for this application, onto which the application scene can be set.
    */
    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
        // Initialize the LibraryManager with your database connection
        DataBase db = new DataBase(); 
        libraryManager = new LibraryManager(db);
        db.startServer();
       
        primaryStage.setTitle("Library Management System");

        // Create main buttons
        Button btnUsers = new Button("Users");
        btnUsers.getStyleClass().add("button-users");
        Button btnBooks = new Button("Books");
        btnBooks.getStyleClass().add("button-books");
        Button btnLoans = new Button("Loans");
        btnLoans.getStyleClass().add("button-loans");

        // HBox to hold main buttons
        HBox mainButtonsBox = new HBox(10, btnUsers, btnBooks, btnLoans);
        mainButtonsBox.setAlignment(Pos.CENTER);
        mainButtonsBox.getStyleClass().add("menu-bar");

        // Footer label
        Label footerLabel = new Label("CY-BOOKS © 2024");
        footerLabel.getStyleClass().add("footer");

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

        // Create the scene and stylize it
        Scene scene = new Scene(root, 800, 600);
        String css = this.getClass().getResource("css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        

        primaryStage.show();
    }

    /**
    * This method is used to display the user operations.
    */
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

    /**
    * This method is used to display the Add User dialog.
    */
    private void showAddUserDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add User");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.getStyleClass().add("dialog-textfield");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("dialog-textfield");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        addressField.getStyleClass().add("dialog-textfield");
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
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to display the Update User dialog.
    */
    private void showUpdateUserDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Update User");

        TextField idField = new TextField();
        idField.setPromptText("User ID");
        idField.getStyleClass().add("dialog-textfield");
        TextField nameField = new TextField();
        nameField.setPromptText("New Name");
        nameField.getStyleClass().add("dialog-textfield");
        TextField emailField = new TextField();
        emailField.setPromptText("New Email");
        emailField.getStyleClass().add("dialog-textfield");
        TextField addressField = new TextField();
        addressField.setPromptText("New Address");
        addressField.getStyleClass().add("dialog-textfield");
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
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to display the Delete User dialog.
    */
    private void showDeleteUserDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Delete User");

        TextField idField = new TextField();
        idField.setPromptText("User ID");
        idField.getStyleClass().add("dialog-textfield");
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
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to display the Search User dialog.
    */
    private void showSearchUserDialog(){
        centerBox.getChildren().clear();
        Button btnById = new Button("By UserID");
        Button btnByEmail = new Button("By Email");

        btnById.setOnAction(e -> showByIdDialog());
        btnByEmail.setOnAction(e -> showByEmailDialog());
        centerBox.getChildren().addAll(btnById,btnByEmail);
    }

    /**
    * This method is used to display the Search by ID dialog.
    */
    private void showByIdDialog(){
        Stage dialog = new Stage();
        dialog.setTitle("By User ID");
    
        TextField IdField = new TextField();
        IdField.setPromptText("ID");
        IdField.getStyleClass().add("dialog-textfield");
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
                URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
                dialogScene.getStylesheets().add(url.toExternalForm());
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });
    
        VBox dialogVBox = new VBox(10, IdField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to display a dialog box that allows the user to search for a user by email.
    * If a user is found, a new dialog box is displayed showing the user's details.
    */
    private void showByEmailDialog(){
        Stage dialog = new Stage();
        dialog.setTitle("By Email");
    
        TextField EmailField = new TextField();
        EmailField.setPromptText("Email");
        EmailField.getStyleClass().add("dialog-textfield");
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
                URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
                dialogScene.getStylesheets().add(url.toExternalForm());
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });
    
        VBox dialogVBox = new VBox(10, EmailField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to display a dialog box that allows the user to search for a user by ID.
    * If a user is found, a new dialog box is displayed showing the user's details and their loans.
    */
    private void showPrintUserProfileDialog(){
        Stage dialog = new Stage();
        dialog.setTitle("Print User Profile");
    
        TextField IdField = new TextField();
        IdField.setPromptText("ID");
        IdField.getStyleClass().add("dialog-textfield");
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
                URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
                dialogScene.getStylesheets().add(url.toExternalForm());
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });
    
        VBox dialogVBox = new VBox(10, IdField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to display three buttons for different book operations: "Search Book", "Return Book", and "Print Loan Book".
    * Each button has an event handler that calls a different method when clicked.
    */
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

    /**
    * This method is used to display three buttons for different search methods: "By ISBN", "By Title", and "By Author".
    * Each button has an event handler that calls a different method when clicked.
    */
    private void showSearchBookDialog() {
        centerBox.getChildren().clear();
        Button btnByISBN = new Button("By ISBN");
        Button btnByTitle = new Button("By Title");
        Button btnByAuthor = new Button("By Author");
        Button btnByDate = new Button("By Date");

        btnByISBN.setOnAction(e -> showByISBNDialog());
        btnByTitle.setOnAction(e -> showByTitleDialog());
        btnByAuthor.setOnAction(e -> showByAuthorDialog());
        btnByDate.setOnAction(e -> showByDateDialog());
        centerBox.getChildren().addAll(btnByISBN, btnByTitle, btnByAuthor, btnByDate);
    }

    /**
    * This method is used to display a dialog box that allows the user to search for a book by ISBN.
    * If a book is found, a new dialog box is displayed showing the book's details.
    */
    private void showByISBNDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("By ISBN");
    
        TextField ISBNField = new TextField();
        ISBNField.setPromptText("ISBN");
        ISBNField.getStyleClass().add("dialog-textfield");
        Button submitButton = new Button("Submit");
    
        submitButton.setOnAction(e -> {
            String books = null;
            try {
                String isbn = ISBNField.getText();
                books = libraryManager.searchBook(isbn, "isbn");
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
    
            if (books != null) {
                List<String> bookList = new ArrayList<>(Arrays.asList(books.split("(?=Title)"))); // split before each "Title"
                
                if (bookList.get(0).isEmpty()) { // if the first element is empty
                    bookList.remove(0); // remove it
                }
    
                Pagination pagination = new Pagination((int) Math.ceil((double) bookList.size() / 4), 0); 
                pagination.getStyleClass().add("dialog-pagination");
                pagination.setPageFactory((pageIndex) -> {
                    int fromIndex = pageIndex * 4; 
                    int toIndex = Math.min(fromIndex + 4, bookList.size()); 
    
                    ListView<String> listView = new ListView<>();
                    listView.getStyleClass().add("dialog-listview");
                    listView.setItems(FXCollections.observableArrayList(bookList.subList(fromIndex, toIndex)));
                    return new BorderPane(listView);
                });
    
                Stage dialog2 = new Stage();
                dialog2.setTitle("View Book");
    
                VBox dialogVBox = new VBox(10, pagination);
                dialogVBox.setAlignment(Pos.CENTER);
                Scene dialogScene = new Scene(dialogVBox, 620, 400); 
                URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
                dialogScene.getStylesheets().add(url.toExternalForm());
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });
    
        VBox dialogVBox = new VBox(10, ISBNField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to show a dialog for searching books by title.
    * It creates a new dialog window with a TextField for the title and a submit button.
    * When the submit button is clicked, it searches for books with the given title.
    * If books are found, it shows another dialog with a list of books.
    * If an error occurs during the search, it shows an alert with the error message.
    */
    private void showByTitleDialog(){
        Stage dialog = new Stage();
        dialog.setTitle("By Title");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        titleField.getStyleClass().add("dialog-textfield");
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            String books = null;
            try{
                String title = titleField.getText();
                books = libraryManager.searchBook(title, "title");
            }
            catch (Exception ex){
                showAlert("Error", ex.getMessage());
            }

            if (books != null) {
                List<String> bookList = new ArrayList<>(Arrays.asList(books.split("(?=Title)"))); // split before each "Title"
                
                if (bookList.get(0).isEmpty()) { // if the first element is empty
                    bookList.remove(0); // remove it
                }

                Pagination pagination = new Pagination((int) Math.ceil((double) bookList.size() / 4), 0);
                pagination.getStyleClass().add("dialog-pagination");
                pagination.setPageFactory((pageIndex) -> {
                    int fromIndex = pageIndex * 4;
                    int toIndex = Math.min(fromIndex + 4, bookList.size());

                    ListView<String> listView = new ListView<>();
                    listView.getStyleClass().add("dialog-listview");
                    listView.setItems(FXCollections.observableArrayList(bookList.subList(fromIndex, toIndex)));
                    return new BorderPane(listView);
                });

                Stage dialog2 = new Stage();
                dialog2.setTitle("View Book");

                VBox dialogVBox = new VBox(10, pagination);
                dialogVBox.setAlignment(Pos.CENTER);
                Scene dialogScene = new Scene(dialogVBox, 800, 425);
                URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
                dialogScene.getStylesheets().add(url.toExternalForm());
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });

        VBox dialogVBox = new VBox(10, titleField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to show a dialog for searching books by title.
    * It creates a new dialog window with a TextField for the title and a submit button.
    * When the submit button is clicked, it searches for books with the given title.
    * If books are found, it shows another dialog with a list of books.
    * If an error occurs during the search, it shows an alert with the error message.
    */
    private void showByAuthorDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("By Author");
    
        TextField AuthorField = new TextField();
        AuthorField.setPromptText("Author");
        AuthorField.getStyleClass().add("dialog-textfield");
        Button submitButton = new Button("Submit");
    
        submitButton.setOnAction(e -> {
            String books = null;
            try {
                String author = AuthorField.getText();
                books = libraryManager.searchBook(author, "author");
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
    
            if (books != null) {
                List<String> bookList = new ArrayList<>(Arrays.asList(books.split("(?=Title)"))); // split before each "Title"
                
                if (bookList.get(0).isEmpty()) { // if the first element is empty
                    bookList.remove(0); // remove it
                }
    
                Pagination pagination = new Pagination((int) Math.ceil((double) bookList.size() / 4), 0); 
                pagination.getStyleClass().add("dialog-pagination");
                pagination.setPageFactory((pageIndex) -> {
                    int fromIndex = pageIndex * 4; 
                    int toIndex = Math.min(fromIndex + 4, bookList.size()); 
    
                    ListView<String> listView = new ListView<>();
                    listView.getStyleClass().add("dialog-listview");
                    listView.setItems(FXCollections.observableArrayList(bookList.subList(fromIndex, toIndex)));
                    return new BorderPane(listView);
                });
    
                Stage dialog2 = new Stage();
                dialog2.setTitle("View Book");
    
                VBox dialogVBox = new VBox(10, pagination);
                dialogVBox.setAlignment(Pos.CENTER);
                Scene dialogScene = new Scene(dialogVBox, 800, 425);
                URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
                dialogScene.getStylesheets().add(url.toExternalForm());
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });
    
        VBox dialogVBox = new VBox(10, AuthorField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to show a dialog for searching books by date.
    * It creates a new dialog window with a TextField for the date and a submit button.
    * When the submit button is clicked, it searches for books with the given date.
    * If books are found, it shows another dialog with a list of books.
    * The list of books is paginated, with 4 books per page.
    * If an error occurs during the search, it shows an alert with the error message.
    */
    private void showByDateDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("By Date");
    
        TextField DateField = new TextField();
        DateField.setPromptText("Date");
        DateField.getStyleClass().add("dialog-textfield");
        Button submitButton = new Button("Submit");
    
        submitButton.setOnAction(e -> {
            String books = null;
            try {
                String date = DateField.getText();
                books = libraryManager.searchBook(date, "date");
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
    
            if (books != null) {
                List<String> bookList = new ArrayList<>(Arrays.asList(books.split("(?=Title)"))); // split before each "Title"
                
                if (bookList.get(0).isEmpty()) { // if the first element is empty
                    bookList.remove(0); // remove it
                }
    
                Pagination pagination = new Pagination((int) Math.ceil((double) bookList.size() / 4), 0); 
                pagination.getStyleClass().add("dialog-pagination");
                pagination.setPageFactory((pageIndex) -> {
                    int fromIndex = pageIndex * 4; 
                    int toIndex = Math.min(fromIndex + 4, bookList.size()); 
    
                    ListView<String> listView = new ListView<>();
                    listView.getStyleClass().add("dialog-listview");
                    listView.setItems(FXCollections.observableArrayList(bookList.subList(fromIndex, toIndex)));
                    return new BorderPane(listView);
                });
    
                Stage dialog2 = new Stage();
                dialog2.setTitle("View Book");
    
                VBox dialogVBox = new VBox(10, pagination);
                dialogVBox.setAlignment(Pos.CENTER);
                Scene dialogScene = new Scene(dialogVBox, 800, 425);
                URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
                dialogScene.getStylesheets().add(url.toExternalForm());
                dialog2.setScene(dialogScene);
                dialog2.show();
            }
        });
    
        VBox dialogVBox = new VBox(10, DateField, submitButton);
        dialogVBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to show a dialog for returning a book.
    * It creates a new dialog window with TextFields for the user ID and ISBN of the book, and a submit button.
    * When the submit button is clicked, it attempts to return the book.
    * If the book is returned successfully, it shows an alert with a success message.
    * If an error occurs during the return, it shows an alert with the error message.
    */
    private void showReturnBookDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Return Book");

        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID");
        userIdField.getStyleClass().add("dialog-textfield");
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        isbnField.getStyleClass().add("dialog-textfield");
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
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to show a dialog with a list of all loaned books.
    * It retrieves the list of loaned books and shows it in a TextArea in the dialog.
    */
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
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }
    private void showLoanOperations() {
        centerBox.getChildren().clear();

        Button btnLoanBook = new Button("Loan Book");
        Button btnPrintLoan = new Button("Print all Loan");
        Button btnReturnLoan = new Button("Print Overdue Loan");
        Button btnRenewLoan = new Button("Print Most Loaned Books Last 30d");

        btnLoanBook.setOnAction(e -> showLoanBookDialog());
        btnPrintLoan.setOnAction(e -> showPrintLoanDialog());
        btnReturnLoan.setOnAction(e -> showReturnLoanDialog());
        btnRenewLoan.setOnAction(e -> showRenewLoanDialog());

        centerBox.getChildren().addAll(btnLoanBook,btnPrintLoan, btnReturnLoan, btnRenewLoan);
    }

    /**
    * This method is used to show a dialog for loaning a book.
    * It creates a new dialog window with TextFields for the user ID and ISBN of the book, and a submit button.
    * When the submit button is clicked, it attempts to loan the book.
    * If the book is loaned successfully, it shows an alert with a success message.
    * If an error occurs during the loan, it shows an alert with the error message.
    */
    private void showLoanBookDialog() {
            Stage dialog = new Stage();
            dialog.setTitle("Loan Book");

            TextField userIdField = new TextField();
            userIdField.setPromptText("User ID");
            userIdField.getStyleClass().add("dialog-textfield");
            TextField isbnField = new TextField();
            isbnField.setPromptText("ISBN");
            isbnField.getStyleClass().add("dialog-textfield");
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
            URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
            dialogScene.getStylesheets().add(url.toExternalForm());
            dialog.setScene(dialogScene);
            dialog.show();
    }
    
    /**
    * This method is used to show a dialog with a list of all loans.
    * It retrieves the list of loans and shows it in a TextArea in the dialog.
    */
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
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to show a dialog with a list of all overdue loans.
    * It retrieves the list of overdue loans and shows it in a TextArea in the dialog.
    */
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
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to show a dialog with a list of the most loaned books in the last 30 days.
    * It retrieves the list of the most loaned books in the last 30 days and shows it in a TextArea in the dialog.
    */
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
        URL url = getClass().getResource("/com/example/cybooks/gui/css/styles.css");
        dialogScene.getStylesheets().add(url.toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
    * This method is used to show an alert with a given title and content.
    * @param title the title of the alert
    * @param content the content of the alert
    */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

