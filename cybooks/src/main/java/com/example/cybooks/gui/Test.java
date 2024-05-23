package com.example.cybooks.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class Test extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
        primaryStage.setTitle("Library Management");

        // Boutons principaux
        Button userButton = new Button("User");
        Button bookButton = new Button("Book");
        Button loansButton = new Button("Loans");

        HBox buttonBox = new HBox(10, userButton, bookButton, loansButton);

        // Root layout
        BorderPane root = new BorderPane();
        root.setTop(buttonBox);

        // TableViews pour chaque section
        TableView<User> userTable = createUserTable();
        TableView<Book> bookTable = createBookTable();
        TableView<Loan> loanTable = createLoanTable();

        // Actions des boutons
        userButton.setOnAction(e -> root.setCenter(userTable));
        bookButton.setOnAction(e -> root.setCenter(bookTable));
        loansButton.setOnAction(e -> root.setCenter(loanTable));

        // Scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @SuppressWarnings("unchecked")
    private TableView<User> createUserTable() {
        TableView<User> table = new TableView<>();

        TableColumn<User, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.getColumns().addAll(nameColumn, emailColumn);
        table.getItems().addAll(
                new User("Alice", "alice@example.com"),
                new User("Bob", "bob@example.com")
        );

        return table;
    }

    @SuppressWarnings("unchecked")
    private TableView<Book> createBookTable() {
        TableView<Book> table = new TableView<>();

        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> editionColumn = new TableColumn<>("Edition");
        editionColumn.setCellValueFactory(new PropertyValueFactory<>("edition"));

        TableColumn<Book, Integer> pagesColumn = new TableColumn<>("Pages");
        pagesColumn.setCellValueFactory(new PropertyValueFactory<>("pages"));

        TableColumn<Book, Integer> copiesColumn = new TableColumn<>("Copies");
        copiesColumn.setCellValueFactory(new PropertyValueFactory<>("copies"));

        table.getColumns().addAll(isbnColumn, titleColumn, editionColumn, pagesColumn, copiesColumn);
        table.getItems().addAll(
                new Book("A", "Misérables", "France", 200, 19),
                new Book("B", "Promesse", "France", 100, 17),
                new Book("D", "L'Étranger", "Maroc", 50, 8),
                new Book("E", "Voyage au bout", "Portugal", 400, 5),
                new Book("F", "Madame Bovary", "Espagne", 200, 5),
                new Book("O", "Nadja", "Algérie", 400, 2),
                new Book("X1", "L'Écume des jours", "Espagne", 100, 5)
        );

        return table;
    }

    @SuppressWarnings("unchecked")
    private TableView<Loan> createLoanTable() {
        TableView<Loan> table = new TableView<>();

        TableColumn<Loan, String> userColumn = new TableColumn<>("User");
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));

        TableColumn<Loan, String> bookColumn = new TableColumn<>("Book");
        bookColumn.setCellValueFactory(new PropertyValueFactory<>("book"));

        TableColumn<Loan, String> loanDateColumn = new TableColumn<>("Loan Date");
        loanDateColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));

        TableColumn<Loan, String> returnDateColumn = new TableColumn<>("Return Date");
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        table.getColumns().addAll(userColumn, bookColumn, loanDateColumn, returnDateColumn);
        table.getItems().addAll(
                new Loan("Alice", "Misérables", "2023-01-01", "2023-01-15"),
                new Loan("Bob", "Promesse", "2023-01-05", "2023-01-20")
        );

        return table;
    }
}

class User {
    private String name;
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

class Book {
    private String isbn;
    private String title;
    private String edition;
    private int pages;
    private int copies;

    public Book(String isbn, String title, String edition, int pages, int copies) {
        this.isbn = isbn;
        this.title = title;
        this.edition = edition;
        this.pages = pages;
        this.copies = copies;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getEdition() {
        return edition;
    }

    public int getPages() {
        return pages;
    }

    public int getCopies() {
        return copies;
    }
}

class Loan {
    private String user;
    private String book;
    private String loanDate;
    private String returnDate;

    public Loan(String user, String book, String loanDate, String returnDate) {
        this.user = user;
        this.book = book;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    public String getUser() {
        return user;
    }

    public String getBook() {
        return book;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public String getReturnDate() {
        return returnDate;
    }
}
