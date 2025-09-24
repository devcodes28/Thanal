package com.example.thanal.util;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneSwitcher {
    public static void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        String fxmlPath = "/com/example/thanal/FXML/" + fxmlFile;
        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneSwitcher.class.getResource(fxmlPath)));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(SceneSwitcher.class.getResource("/com/example/thanal/css/style.css")).toExternalForm());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}