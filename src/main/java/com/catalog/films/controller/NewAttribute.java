package com.catalog.films.controller;

import com.catalog.films.data.DatabaseHandler;
import com.catalog.films.enums.TypeSearch;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Data;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

@Data
public class NewAttribute implements Initializable {


    DatabaseHandler dbHandler = new DatabaseHandler();


    private Stage dialogStage;
    @FXML
    private TextField newValue;
    @FXML
    public ComboBox<TypeSearch> attribute;

    @FXML
    private void ok() {
        if (newValue != null && !newValue.getText().isEmpty()) {
            try {
                dbHandler.insertAttribute(attribute.getValue(), newValue.getText());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            dialogStage.close();
        }
    }

    @FXML
    public Button ok;

    @FXML
    private void cancel() {
        dialogStage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        newValue.disableProperty().bind(attribute.getSelectionModel().selectedItemProperty().isNull());
        ok.disableProperty().bind(attribute.getSelectionModel().selectedItemProperty().isNull());
        ok.disableProperty().bind(Bindings.isEmpty(newValue.textProperty()));
        attribute.getItems().setAll(TypeSearch.GENRE, TypeSearch.PRODUCER, TypeSearch.ACTOR, TypeSearch.YEAR);
    }
}
