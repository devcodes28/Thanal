package com.example.thanal.controller;

import com.example.thanal.main.ThanalApp; // Import ThanalApp to check init status
import com.example.thanal.model.*;
import com.example.thanal.util.SceneSwitcher;
import com.example.thanal.util.SessionManager;
import javafx.application.Platform; // Import Platform
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*; // Import Alert
import java.io.IOException;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient; // Correct import
public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> userRoleComboBox;

    @FXML
    public void initialize() {
        userRoleComboBox.getItems().addAll("Parent", "Doctor", "Supporter", "Admin");
        statusLabel.setText("");
        statusLabel.setWrapText(true);
    }

    @FXML
    void handleLoginButtonAction(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = userRoleComboBox.getValue();

        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: red;");

        // --- Input Validation ---
        if (role == null || role.isEmpty()) {
            statusLabel.setText("Please select a user role."); return;
        }
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Email and password cannot be empty."); return;
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            statusLabel.setText("Please enter a valid email format."); return;
        }

        // --- Check Firebase Initialization ---
        if (!ThanalApp.isFirebaseInitialized()) {
            statusLabel.setText("Error: Backend connection not available.");
            System.err.println("Login attempt failed: Firebase not initialized.");
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Cannot connect to backend services. Please restart the application or check connection.");
            return;
        }

        // --- Authenticate ---
        User loggedInUser = authenticate(email, password, role);

        // --- Handle Login Result ---
        if (loggedInUser != null) {
            SessionManager.getInstance().setCurrentUser(loggedInUser);
            statusLabel.setStyle("-fx-text-fill: green;"); // Success color
            statusLabel.setText("Login Successful!");
            System.out.println("Login success for: " + email + " as " + role);
            try {
                String dashboardFile = loggedInUser.getRole().toLowerCase() + "-dashboard.fxml";
                SceneSwitcher.switchScene(event, dashboardFile);
            } catch (Exception e) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Error loading dashboard for " + role + ".");
                System.err.println("Error loading/switching scene: " + loggedInUser.getRole().toLowerCase() + "-dashboard.fxml");
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the dashboard view. [" + e.getMessage() + "]");
            }
        } else {
            if (statusLabel.getText().isEmpty() || !statusLabel.getText().contains("Error connecting")){
                statusLabel.setText("Invalid credentials or user not approved.");
            }
            System.out.println("Login failed for: " + email + " as " + role);
        }
    }

    private User authenticate(String email, String password, String role) {
        if ("Admin".equals(role) && "admin@thanal.com".equals(email) && "pass".equals(password)) {
            Admin a = new Admin(); a.setName("Admin User"); a.setEmail(email); a.setRole("admin");
            return a;
        }

        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<DocumentSnapshot> future = db.collection("users").document(email).get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                String storedPassword = document.getString("password");
                String storedRole = document.getString("role");
                String selectedRoleLower = role.toLowerCase();
                if (storedPassword != null && storedPassword.equals(password) &&
                        storedRole != null && storedRole.equals(selectedRoleLower)) {
                    switch (storedRole) {
                        case "parent":
                            Parent p = document.toObject(Parent.class);
                            if (p != null) { p.setRole(storedRole); p.setEmail(email); return p; }
                            break;
                        case "doctor":
                            Doctor d = document.toObject(Doctor.class);
                            if (d != null) { d.setRole(storedRole); d.setEmail(email); return d; }
                            break;
                        case "supporter":
                            Supporter s = document.toObject(Supporter.class);
                            if (s != null) { s.setRole(storedRole); s.setEmail(email); return s; }
                            break;
                        default:
                            System.err.println("Unknown role in 'users' collection: " + storedRole);
                            statusLabel.setText("Login failed: User data corrupted (invalid role).");
                            return null;
                    }
                    System.err.println("Failed to map Firestore document to User object for role: " + storedRole);
                    statusLabel.setText("Login failed: Could not load user profile.");
                    return null;

                } else {
                    System.out.println("Authentication failed: Password or role mismatch for " + email);
                    statusLabel.setText("Invalid credentials for the selected role.");
                }
            } else {
                System.out.println("User not found or not approved: " + email);
                statusLabel.setText("User not found or not yet approved by admin.");
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Database error during login. Please try again.");
            System.err.println("Firestore error during login: " + e.getMessage());
            return null;
        }
    }

    // --- Navigation and Utility Methods ---
    @FXML void handleBackButtonAction(ActionEvent event) throws IOException { SceneSwitcher.switchScene(event, "home-page.fxml"); }

    // --- THIS IS THE MODIFIED METHOD ---
    @FXML
    void handleForgotPassword(ActionEvent event) {
        System.out.println("Forgot Password clicked!");
        try {
            SceneSwitcher.switchScene(event, "reset-password.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the password reset page.");
        }
    }

    @FXML void handleGoToRegister(ActionEvent event) throws IOException { SceneSwitcher.switchScene(event, "registration-page.fxml"); }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}