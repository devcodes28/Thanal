package com.example.thanal.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ThanalApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // This path must exactly match the location of your FXML file in the 'resources' folder.
        // The leading "/" makes the path absolute from the root of the classpath.
        String fxmlPath = "/com/example/thanal/login.fxml";
        URL fxmlLocation = getClass().getResource(fxmlPath);

        if (fxmlLocation == null) {
            System.err.println("Cannot find FXML file: " + fxmlPath);
            System.err.println("Please ensure the file exists at 'src/main/resources" + fxmlPath + "' and that the resources folder is configured correctly in your build path.");
            // Or show an alert dialog to the user
            // Alert alert = new Alert(Alert.AlertType.ERROR, "Could not find UI definition file. The application cannot start.");
            // alert.showAndWait();
            return;
        }

        Parent root = FXMLLoader.load(fxmlLocation);
        Scene scene = new Scene(root);
        stage.setTitle("Thanal - Autism Support");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
