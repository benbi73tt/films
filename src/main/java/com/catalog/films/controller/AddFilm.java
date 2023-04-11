package com.catalog.films.controller;

import com.catalog.films.data.DatabaseHandler;
import com.catalog.films.model.Film;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Data;


@Data
public class AddFilm {

    private Stage dialogStage;
    @FXML
    private ComboBox<String> producerBox;
    @FXML
    private TextField nameBox;
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
        return genreBox.getValue() == null || producerBox.getValue() == null || actorBox.getValue() == null || yearBox.getValue() == null;
    }

    private boolean isCheckNumber(String name) {
        return name.matches("[0-9.]+");
    }

    private boolean isCheckRate(String rate) {
        double result = Double.parseDouble(rate);
        return (result > 0) && (result <= 10);
    }

    @FXML
    private void save() {
        if (checkIsEmpty() || nameBox.getText().isBlank() || !isCheckNumber(ratingField.getText()) || !(isCheckRate(ratingField.getText()))) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Ввели неккоректные данные. Попробуйте снова...", ButtonType.CANCEL);
            alert.showAndWait();
        } else {
            Film film = new Film(nameBox.getText(),
                    ratingField.getText(),
                    genreBox.getValue(),
                    producerBox.getValue(),
                    yearBox.getValue(),
                    actorBox.getValue());
            dbHandler.insertFilm(film);
            nameBox.clear();
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
}
