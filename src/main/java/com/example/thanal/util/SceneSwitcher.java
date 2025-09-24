package com.example.thanal.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    /**
     * Switches the scene in the main window.
     * @param event The ActionEvent that triggered the scene switch.
     * @param fxmlFile The name of the FXML file to load.
     * @param isResizable Whether the new scene should be resizable.
     */
    public static void switchScene(ActionEvent event, String fxmlFile, boolean isResizable) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("/com/example/thanal/" + fxmlFile));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Load CSS for the new scene
        scene.getStylesheets().add(SceneSwitcher.class.getResource("/com/example/thanal/style.css").toExternalForm());

        stage.setResizable(isResizable);

        // If not resizable, set min/max size to current scene size to prevent manual resizing
        if (!isResizable) {
            stage.setMinWidth(stage.getWidth());
            stage.setMaxWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
            stage.setMaxHeight(stage.getHeight());
        } else {
            // Reset min/max if becoming resizable
            stage.setMinWidth(200); // Sensible minimum
            stage.setMaxWidth(Double.MAX_VALUE);
            stage.setMinHeight(200);
            stage.setMaxHeight(Double.MAX_VALUE);
        }

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}