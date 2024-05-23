module com.example.cybooks {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;

    opens com.example.cybooks.gui to javafx.graphics;   
    exports com.example.cybooks.gui;
    
}