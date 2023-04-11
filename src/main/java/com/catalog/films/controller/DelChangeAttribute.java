package com.catalog.films.controller;

import com.catalog.films.data.DatabaseHandler;
import com.catalog.films.enums.TypeSearch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Data;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

@Data
public class DelChangeAttribute implements Initializable {


    DatabaseHandler dbHandler = new DatabaseHandler();


    private Stage dialogStage;

    private Boolean isChange;

    @FXML
    private Label labelChange;
    @FXML
    private TextField changeValue;
    @FXML
    private ComboBox<String> delValue;
    @FXML
    public ComboBox<TypeSearch> attribute;

    ObservableList<String> genre = FXCollections.observableArrayList();
    ObservableList<String> actor = FXCollections.observableArrayList();
    ObservableList<String> name = FXCollections.observableArrayList();
    ObservableList<String> producer = FXCollections.observableArrayList();
    ObservableList<String> year = FXCollections.observableArrayList();

    @FXML
    private void ok() {
        try {
            int id = dbHandler.searchAttribute(delValue.getValue(), attribute.getValue());
            if (!isChange) {
                dbHandler.deleteAttribute(id, attribute.getValue());
            } else if (changeValue.getText() != null) {
                dbHandler.changeAttribute(id, attribute.getValue(), changeValue.getText());
            } else if (changeValue.getText() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Ввели пустое значение. Попробуйте снова...", ButtonType.CANCEL);
                alert.showAndWait();
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.CANCEL);
            alert.showAndWait();
        }
        dialogStage.close();
    }

    @FXML
    public Button ok;

    @FXML
    private void cancel() {
        dialogStage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        attribute.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            delValue.setItems(switch (newValue) {
                case NAME, RATE -> null;
                case YEAR -> year;
                case PRODUCER -> producer;
                case GENRE -> genre;
                case ACTOR -> actor;
            });
            if (isChange) {
                ok.setText("Сохранить");
                changeValue.setVisible(true);
                labelChange.setVisible(true);
            }
        });

        delValue.disableProperty().bind(attribute.getSelectionModel().selectedItemProperty().isNull());
        ok.disableProperty().bind(attribute.getSelectionModel().selectedItemProperty().isNull());
        ok.disableProperty().bind(delValue.getSelectionModel().selectedItemProperty().isNull());
        attribute.getItems().setAll(TypeSearch.GENRE, TypeSearch.PRODUCER, TypeSearch.ACTOR, TypeSearch.YEAR);
    }
}
