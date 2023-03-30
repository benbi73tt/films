package com.catalog.films.controller;

import com.catalog.films.Application;
import com.catalog.films.model.Film;
import com.catalog.films.data.DatabaseHandler;
import com.catalog.films.enums.TypeSearch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Data;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

@Data
public class BaseController implements Initializable {
    //TODO Добавить изменение актеров
    //TODO Заполнить БД
    //TODO Обновление таблиц при заполнение добавлении
    //TODO активная сортировка при вводе???
    //TODO Вынести в отдельный экран????
    //TODO сделать красиво
    @FXML
    public TableView<Film> CatalogTable;
    @FXML
    public TableColumn<Film, String> idColumn;
    @FXML
    public TableColumn<Film, String> nameColumn;
    @FXML
    public TableColumn<Film, String> yearColumn;
    @FXML
    public TableColumn<Film, String> ratingColumn;
    @FXML
    public TableColumn<Film, String> genreColumn;
    @FXML
    public TableColumn<Film, String> actorColumn;
    @FXML
    public TableColumn<Film, String> producerColumn;


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


    @FXML
    public Button addButton;

    @FXML
    public Button updateCatalog;


    @FXML
    public TableColumn<Film, String> actorSearchColumn;
    @FXML
    public TableColumn<Film, String> genreSearchColumn;
    @FXML
    public TableColumn<Film, String> nameSearchColumn;
    @FXML
    public TableColumn<Film, String> ratingSearchColumn;
    @FXML
    public TableColumn<Film, String> producerSearchColumn;
    @FXML
    public TableColumn<Film, String> yearSearchColumn;
    @FXML
    public TableView<Film> searchTable;
    @FXML
    public Button searchButton;
    @FXML
    public Button delete;
    @FXML
    public Button change;
    @FXML
    public Button addAttribute;

    DatabaseHandler dbHandler = new DatabaseHandler();

    private final ObservableList<Film> data = FXCollections.observableArrayList();
    ObservableList<String> genre = FXCollections.observableArrayList();
    ObservableList<String> actor = FXCollections.observableArrayList();
    ObservableList<String> name = FXCollections.observableArrayList();
    ObservableList<String> producer = FXCollections.observableArrayList();
    ObservableList<String> year = FXCollections.observableArrayList();


    public boolean isCheckNumber(String name) {
        return name.matches("[0-9.]+");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        searchButton.setOnAction(actionEvent -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Application.class.getResource("searchAttribute.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.getRoot()));
            SearchAttribute controller = loader.getController();
            controller.initData(searchTable);
            controller.setDialogStage(stage);
            stage.showAndWait();
            updateAttribute();
        });

        addAttribute.setOnAction(actionEvent -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Application.class.getResource("addAttribute.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.getRoot()));
            NewAttribute controller = loader.getController();
            controller.setDialogStage(stage);
            stage.showAndWait();
            updateAttribute();
        });



        addButton.setOnAction(actionEvent -> {
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
                data.add(film);
                dbHandler.insertFilm(film);
                nameBox.setValue(null);
                ratingField.clear();
                producerBox.setValue(null);
                yearBox.setValue(null);
                actorBox.setValue(null);
                genreBox.setValue(null);
            }
        });

        delete.setOnAction(actionEvent -> {
            int selectedIndex = CatalogTable.getSelectionModel().getSelectedIndex();
            try {
                delete(CatalogTable.getItems().get(selectedIndex).getId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            CatalogTable.getItems().remove(selectedIndex);

        });

        change.setOnAction(actionEvent -> {
            int selectedIndex = CatalogTable.getSelectionModel().getSelectedIndex();
            int filmId = CatalogTable.getItems().get(selectedIndex).getId();
            try {
                if (!(nameBox.getValue() == null)) {
                    change(filmId, nameBox.getValue(), TypeSearch.NAME);
                }
                if (!ratingField.getText().isBlank() && isCheckNumber(ratingField.getText())) {
                    change(filmId, ratingField.getText(), TypeSearch.RATE);
                }
                if (!(yearBox.getValue() == null)) {
                    change(filmId, yearBox.getValue(), TypeSearch.YEAR);
                }
                if (!(genreBox.getValue() == null)) {
                    change(filmId, genreBox.getValue(), TypeSearch.GENRE);
                }
                if (!(producerBox.getValue() == null)) {
                    change(filmId, producerBox.getValue(), TypeSearch.PRODUCER);
                }
                if (!(actorBox.getValue() == null)) {
                    change(filmId, actorBox.getValue(), TypeSearch.ACTOR);
                }
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Ввели неккоректные данные. Попробуйте снова...", ButtonType.CANCEL);
                alert.showAndWait();
                throw new RuntimeException(e);

            }
        });

        updateCatalog.setOnAction(actionEvent -> {
            data.clear();
            searchTable.getItems().clear();
            addInfFilms();
            CatalogTable.setItems(data);
            updateAttribute();
        });

        updateAttribute();
        idColumn.setVisible(false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        producerColumn.setCellValueFactory(new PropertyValueFactory<>("producer"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        actorColumn.setCellValueFactory(new PropertyValueFactory<>("actor"));

        nameSearchColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        yearSearchColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        ratingSearchColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        producerSearchColumn.setCellValueFactory(new PropertyValueFactory<>("producer"));
        genreSearchColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        actorSearchColumn.setCellValueFactory(new PropertyValueFactory<>("actor"));

    }

    private void updateAttribute(){
        try {

            updateGenre();
            updateActor();
            updateName();
            updateProducer();
            updateYear();

            genreBox.getItems().setAll(genre.sorted());
            actorBox.getItems().setAll(actor.sorted());
            nameBox.getItems().setAll(name.sorted());
            producerBox.getItems().setAll(producer.sorted());
            yearBox.getItems().setAll(year.sorted());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void delete(int id) throws SQLException {
        dbHandler.delete(id);
    }

    private void change(int id, String text, TypeSearch typeSearch) throws SQLException {
        dbHandler.change(id, text, typeSearch);
    }

    private boolean checkIsEmpty() {
        return genreBox.getValue() == null || nameBox.getValue() == null || producerBox.getValue() == null || actorBox.getValue() == null || yearBox.getValue() == null;
    }

    private void updateGenre() throws SQLException {
        ResultSet resultGenre = dbHandler.getAttribute(TypeSearch.GENRE);
        genre.clear();
        while (resultGenre.next()) {
            genre.add(resultGenre.getString(2));
        }
    }

    private void updateActor() throws SQLException {
        ResultSet resultGenre = dbHandler.getAttribute(TypeSearch.ACTOR);
        actor.clear();
        while (resultGenre.next()) {
            actor.add(resultGenre.getString(2));
        }
    }

    private void updateName() throws SQLException {
        ResultSet resultName = dbHandler.getAttribute(TypeSearch.NAME);
        name.clear();
        while (resultName.next()) {
            name.add(resultName.getString(2));
        }
    }

    private void updateProducer() throws SQLException {
        ResultSet resultProducer = dbHandler.getAttribute(TypeSearch.PRODUCER);
        producer.clear();
        while (resultProducer.next()) {
            producer.add(resultProducer.getString(2));
        }
    }
    private void updateYear() throws SQLException {
        ResultSet resultYear = dbHandler.getAttribute(TypeSearch.YEAR);
        year.clear();
        while (resultYear.next()) {
            year.add(resultYear.getString(2));
        }
    }

    private void addInfFilms() {
        ResultSet films = dbHandler.getFilm();
        try {
            while (films.next()) {
                StringBuilder listActor = new StringBuilder();
                ResultSet actor = dbHandler.getActorFilms();

                while (actor.next()) {
                    if (Objects.equals(actor.getString(1), films.getString(2))) {
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
                data.add(film);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
