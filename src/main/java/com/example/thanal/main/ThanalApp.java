package com.example.thanal.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ThanalApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/thanal/FXML/home-page.fxml")));

        // --- CHANGE 1: Set the fixed scene size ---
        Scene scene = new Scene(root, 1280, 800);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/thanal/css/style.css")).toExternalForm());

        stage.setTitle("Thanal - Autism Support");
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/thanal/images/logo.png")));
        stage.getIcons().add(icon);

        // --- CHANGE 2: Make the window NON-resizable ---
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}