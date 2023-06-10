module com.example.kur {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.kur to javafx.fxml;
    exports com.example.kur;
    exports Controller;
    opens Controller to javafx.fxml;
    exports dao;
    opens dao to javafx.fxml;
}