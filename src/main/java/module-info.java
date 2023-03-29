module com.catalog.films {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.sql;


    opens com.catalog.films to javafx.fxml;
    exports com.catalog.films;
    exports com.catalog.films.controller;
    opens com.catalog.films.controller to javafx.fxml;
    exports com.catalog.films.data;
    opens com.catalog.films.data to javafx.fxml;
    exports com.catalog.films.model;
    opens com.catalog.films.model to javafx.fxml;
}