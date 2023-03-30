package com.catalog.films.controller;

import com.catalog.films.data.DatabaseHandler;
import com.catalog.films.enums.TypeSearch;
import com.catalog.films.model.Film;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Data;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;


@Data
public class AddFilm implements Initializable {


    private Stage dialogStage;
    @FXML
    private ComboBox<String> producerBox;
    @FXML
    private ComboBox<String> nameBox;
    @FXML
    private ComboBox<String> yearBox;
    @FXML
    public TextField ratingField;
    @FXML
    private ComboBox<String> genreBox;
    @FXML
    private ComboBox<String> actorBox;
    DatabaseHandler dbHandler = new DatabaseHandler();

    private boolean checkIsEmpty() {
        return genreBox.getValue() == null || nameBox.getValue() == null || producerBox.getValue() == null || actorBox.getValue() == null || yearBox.getValue() == null;
    }

    private boolean isCheckNumber(String name) {
        return name.matches("[0-9.]+");
    }

    @FXML
    private void save() {
        if (checkIsEmpty() || !isCheckNumber(ratingField.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Ввели неккоректные данные. Попробуйте снова...", ButtonType.CANCEL);
            alert.showAndWait();
        } else {
            Film film = new Film(nameBox.getValue(),
                    ratingField.getText(),
                    genreBox.getValue(),
                    producerBox.getValue(),
                    yearBox.getValue(),
                    actorBox.getValue());
            dbHandler.insertFilm(film);
            nameBox.setValue(null);
            ratingField.clear();
            producerBox.setValue(null);
            yearBox.setValue(null);
            actorBox.setValue(null);
            genreBox.setValue(null);
            dialogStage.close();
        }
    }

    @FXML
    private void cancel() {
        dialogStage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

}
