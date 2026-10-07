module com.example {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens com.example to javafx.fxml;
    exports com.example;
}