package com.example.thanal.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ThanalApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        String fxmlPath = "/com/example/thanal/home-page.fxml";
        URL fxmlLocation = getClass().getResource(fxmlPath);

        if (fxmlLocation == null) {
            System.err.println("Cannot find FXML file: " + fxmlPath);
            return;
        }

        Parent root = FXMLLoader.load(fxmlLocation);
        Scene scene = new Scene(root, 900, 650); // Set a default size

        // Load the CSS stylesheet
        scene.getStylesheets().add(getClass().getResource("/com/example/thanal/style.css").toExternalForm());

        stage.setTitle("Thanal - Autism Support");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/thanal/logo.png")));
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}