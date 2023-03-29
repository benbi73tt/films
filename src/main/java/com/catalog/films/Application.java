package com.catalog.films;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;

@Data
public class Application extends javafx.application.Application {
    private Stage primaryStage;
    private AnchorPane rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Catalog Films");
        showBaseWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void showBaseWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Application.class.getResource("catalogList.fxml"));
        rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        InputStream iconStream = getClass().getResourceAsStream("/icons/someImage.png");
        Image image = new Image(iconStream);
        primaryStage.getIcons().add(image);
        primaryStage.show();
    }
}