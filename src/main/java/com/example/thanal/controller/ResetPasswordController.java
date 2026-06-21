package com.example.thanal.controller;

import com.example.thanal.main.ThanalApp;
import com.example.thanal.util.SceneSwitcher;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ResetPasswordController {

    @FXML private Label statusLabel;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField1;
    @FXML private PasswordField passwordField2;

    @FXML
    public void initialize() {
        statusLabel.setText("");
        statusLabel.setWrapText(true);
    }

    @FXML
    void handleResetPassword(ActionEvent event) {
        String email = emailField.getText();
        String phone = phoneField.getText();
        String newPassword = passwordField1.getText();
        String confirmPassword = passwordField2.getText();

        // --- Validation ---
        if (isNullOrEmpty(email) || isNullOrEmpty(phone) || isNullOrEmpty(newPassword)) {
            showError("Please fill in all fields."); return;
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            showError("Please enter a valid email format."); return;
        }
        if (!phone.matches("\\d{10}")) {
            showError("Please enter a valid 10-digit phone number."); return;
        }
        if (newPassword.length() < 6) {
            showError("New password must be at least 6 characters."); return;
        }
        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match."); return;
        }
        if (!ThanalApp.isFirebaseInitialized()) {
            showError("Cannot connect to backend services."); return;
        }

        // --- Firestore Logic ---
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference userRef = db.collection("users").document(email);
            ApiFuture<DocumentSnapshot> future = userRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                String storedPhone = document.getString("phone");

                if (phone.equals(storedPhone)) {
                    ApiFuture<WriteResult> updateFuture = userRef.update("password", newPassword);
                    updateFuture.get();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Your password has been reset successfully. Please log in.");
                    handleBackToLogin(event); // Go back to login screen

                } else {
                    showError("Phone number does not match our records for this email.");
                }
            } else {
                showError("Email not found in our system.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred: " + e.getMessage());
        }
    }

    @FXML
    void handleBackToLogin(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "login.fxml");
    }

    // --- Helper Methods ---
    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setText(message);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Platform.runLater(() -> new Alert(type, msg, ButtonType.OK){{
            setTitle(title);
            setHeaderText(null);
        }}.showAndWait());
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}