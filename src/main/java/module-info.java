module com.example.registro_elettronico {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires java.desktop;


    opens com.example.project to javafx.fxml;
    exports com.example.project;
}
