package com.example.thanal.main;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import javafx.application.Application;
import javafx.application.Platform; // Import Platform
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
public class ThanalApp extends Application {
    private static boolean firebaseInitialized = false; // Flag to track initialization
    @Override
    public void start(Stage stage) throws IOException {
        // --- Initialize Firebase (Only Once) ---
        if (!firebaseInitialized) {
            try {
                InputStream serviceAccount = getClass().getResourceAsStream("/com/example/thanal/serviceAccountKey.json");

                if (serviceAccount == null) {
                    serviceAccount = ThanalApp.class.getClassLoader().getResourceAsStream("com/example/thanal/serviceAccountKey.json");
                }
                if (serviceAccount == null) {
                    throw new IOException("CRITICAL ERROR: 'serviceAccountKey.json' not found in resources/com/example/thanal/. Ensure it's copied correctly.");
                }

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    firebaseInitialized = true; // Set flag on success
                    System.out.println("Firebase initialized successfully!");
                } else {
                    firebaseInitialized = true; // Already initialized
                    System.out.println("Firebase instance already exists.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("CRITICAL: Failed to initialize Firebase. Backend features will be disabled.");
                // Show error alert on JavaFX thread
                showInitializationErrorAlert(e);
                // Do NOT set firebaseInitialized = true if it fails
            }
        }
        // --- End Firebase Initialization ---
        URL homePageUrl = Objects.requireNonNull(getClass().getResource("/com/example/thanal/FXML/home-page.fxml"),
                "Cannot find home-page.fxml in resources/com/example/thanal/FXML/");
        Parent root = FXMLLoader.load(homePageUrl);
        Scene scene = new Scene(root, 1280, 800);
        URL cssUrl = Objects.requireNonNull(getClass().getResource("/com/example/thanal/css/style.css"),
                "Cannot find style.css in resources/com/example/thanal/css/");
        scene.getStylesheets().add(cssUrl.toExternalForm());
        stage.setTitle("Thanal - Autism Support");
        URL iconUrl = Objects.requireNonNull(getClass().getResource("/com/example/thanal/images/logo.png"),
                "Cannot find logo.png in resources/com/example/thanal/images/");
        Image icon = new Image(iconUrl.toExternalForm());
        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
    public static boolean isFirebaseInitialized() {
        return firebaseInitialized;
    }
    private void showInitializationErrorAlert(Exception e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Critical Error");
            alert.setHeaderText("Firebase Initialization Failed");
            alert.setContentText("Could not connect to backend services. Features requiring database access will be disabled.\n" +
                    "Check console logs for details (e.g., serviceAccountKey.json path, internet connection).\n\n" +
                    "Error: " + e.getMessage());
            alert.showAndWait();
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}