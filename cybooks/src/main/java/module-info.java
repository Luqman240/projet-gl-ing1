module com.example.cybooks {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;

    opens com.exemple.cybooks.gui to javafx.graphics;   
    exports com.exemple.cybooks.gui;
    
}