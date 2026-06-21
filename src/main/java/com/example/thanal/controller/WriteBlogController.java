package com.example.thanal.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp; // Use Timestamp
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp; // Import FirebaseApp
import com.google.firebase.cloud.FirestoreClient;
import com.example.thanal.main.ThanalApp; // Import ThanalApp
import com.example.thanal.model.User; // To get author info
import com.example.thanal.util.SceneSwitcher;
import com.example.thanal.util.SessionManager; // To get author info
import javafx.application.Platform; // <-- *** IMPORT IS HERE ***
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType; // Import ButtonType
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService; // Import ExecutorService
import java.util.concurrent.Executors; // Import Executors

public class WriteBlogController {

    @FXML private TextField blogTitleField;
    @FXML private TextArea blogContentArea;
    // Executor for background Firestore tasks
    private final ExecutorService firestoreExecutor = Executors.newSingleThreadExecutor();

    @FXML
    public void initialize() {
        Platform.runLater(() -> blogTitleField.requestFocus());
    }

    @FXML
    void handlePublishBlog(ActionEvent event) {
        String title = blogTitleField.getText();
        String content = blogContentArea.getText();
        User currentUser = SessionManager.getInstance().getCurrentUser(); // Get logged-in user
        if (isNullOrEmpty(title) || isNullOrEmpty(content)) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in both the blog title and content."); return;
        }
        if (currentUser == null || isNullOrEmpty(currentUser.getEmail())) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error", "Cannot publish blog: current user not identified."); return;
        }
        if (!ThanalApp.isFirebaseInitialized()) { // Use check method
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Cannot publish blog: backend connection not ready."); return;
        }

        try {
            Firestore db = FirestoreClient.getFirestore();
            Map<String, Object> blogData = new HashMap<>();
            blogData.put("title", title);
            blogData.put("content", content);
            blogData.put("authorId", currentUser.getUserId() != null ? currentUser.getUserId() : -1L);
            blogData.put("authorEmail", currentUser.getEmail());
            blogData.put("authorName", currentUser.getName());
            blogData.put("createdAt", Timestamp.now()); // Add a creation timestamp

            ApiFuture<WriteResult> future = db.collection("blogs").document().set(blogData);
            future.addListener(() -> {
                try {
                    future.get();
                    System.out.println("Blog saved successfully!");
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Blog post published!");
                        try {
                            handleBack(event);
                        } catch (IOException e) {
                            System.err.println("Error navigating back after blog save: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error confirming blog save: " + e.getMessage());
                    e.printStackTrace();
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Save Error", "Failed to save blog post: " + e.getMessage()));
                }
            }, firestoreExecutor);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to initiate blog publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleBack(ActionEvent event) throws IOException {
        User user = SessionManager.getInstance().getCurrentUser();
        String dashboardFile = "home-page.fxml"; // Default fallback
        if (user != null && !isNullOrEmpty(user.getRole())) {
            dashboardFile = user.getRole().toLowerCase() + "-dashboard.fxml";
        }
        try {
            SceneSwitcher.switchScene(event, dashboardFile);
        } catch (IOException | NullPointerException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not return to the dashboard ("+dashboardFile+"). Returning home.");
            e.printStackTrace();
            SceneSwitcher.switchScene(event, "home-page.fxml");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showActualAlert(alertType, title, message));
        } else {
            showActualAlert(alertType, title, message);
        }
    }
    private void showActualAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }


    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}