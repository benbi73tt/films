package com.catalog.films.controller;

import com.catalog.films.data.DatabaseHandler;
import com.catalog.films.enums.OperatorEnum;
import com.catalog.films.enums.TypeSearch;
import com.catalog.films.model.Film;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Data;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

@Data
public class SearchAttribute implements Initializable {

    @FXML
    public ComboBox<TypeSearch> attribute;
    @FXML
    public ComboBox<OperatorEnum> operator;
    @FXML
    public Label labelOperator;
    @FXML
    public TextField textSearch;
    @FXML
    public Button search;

    @FXML
    public TableView<Film> searchTable;
    DatabaseHandler dbHandler = new DatabaseHandler();

    private Stage dialogStage;

    @FXML
    private void search() {
        if (textSearch != null && !textSearch.getText().isEmpty()) {
            getSearchAttribute(attribute.getValue(), textSearch.getText(), operator.getValue());
            dialogStage.close();
        }
    }

    @FXML
    private void cancel() {
        dialogStage.close();
    }

    private final ObservableList<Film> searchData = FXCollections.observableArrayList();

    private void getSearchAttribute(TypeSearch type, String text, OperatorEnum operator) {
        searchTable.getItems().clear();
        ResultSet films = dbHandler.getSearchAttribute(text, type, operator);
        try {
            while (films.next()) {
                StringBuilder listActor = new StringBuilder();
                ResultSet actor = dbHandler.getActorFilms();

                while (actor.next()) {
                    if (Objects.equals(actor.getString(1), films.getString(1))) {
                        listActor.append(actor.getString(2)).append(", ");
                    }
                }
                Film film = new Film(films.getInt(1),
                        films.getString(2),
                        films.getString(3),
                        films.getString(4),
                        films.getString(5),
                        films.getString(6),
                        listActor.toString());
                if (type == TypeSearch.ACTOR) {
                    if (film.getActor().toLowerCase().contains(text.toLowerCase())) {
                        searchData.add(film);
                    }
                } else {
                    searchData.add(film);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        searchTable.setItems(searchData);
    }

    void initData(TableView<Film> searchTable) {
        this.searchTable = searchTable;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        operator.getItems().setAll(OperatorEnum.values());
        operator.getSelectionModel().selectFirst();
        attribute.getItems().setAll(TypeSearch.values());
        attribute.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            operator.setDisable(!(newValue == TypeSearch.RATE || newValue == TypeSearch.YEAR));
            labelOperator.setDisable(!(newValue == TypeSearch.RATE || newValue == TypeSearch.YEAR));
            textSearch.setDisable(false);
            textSearch.clear();
        });

        search.disableProperty().bind(Bindings.isEmpty(textSearch.textProperty()));
    }
}
