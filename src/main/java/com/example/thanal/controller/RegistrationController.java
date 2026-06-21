package com.example.thanal.controller;

import com.example.thanal.main.ThanalApp; // Import ThanalApp
import com.example.thanal.util.SceneSwitcher;
import javafx.application.Platform; // Import Platform
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient; // Correct import
import java.util.HashMap;
import java.util.Map;

public class RegistrationController {

    private static final String LOCAL_UPLOAD_DIR = "C:/Thanal_Uploads/"; // Ensure this folder is writable

    // --- FXML Fields (Ensure these match fx:id in FXML) ---
    @FXML private ComboBox<String> userRoleComboBox;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField1;
    @FXML private PasswordField passwordField2;
    @FXML private Label aadhaarLabel;
    @FXML private Label statusLabel;

    // Role Specific VBoxes
    @FXML private VBox parentFields;
    @FXML private VBox doctorFields;
    @FXML private VBox supporterFields;

    // Parent Fields
    @FXML private TextField childNameField;
    @FXML private DatePicker childDobPicker;
    @FXML private Label medicalReportLabel;
    @FXML private Label disabilityReportLabel;

    // Doctor Fields
    @FXML private TextField specializationField;
    @FXML private TextField licenseNoField;        // Check fx:id in FXML
    @FXML private Label licenseLabel;          // Check fx:id in FXML

    // Supporter Fields
    @FXML private TextField organizationField;     // Added

    // File variables
    private File aadhaarFile;
    private File medicalReportFile;
    private File disabilityReportFile;
    private File licenseFile;
    // --- End FXML Fields ---

    @FXML
    public void initialize() {
        userRoleComboBox.getItems().addAll("Parent", "Doctor", "Supporter");
        userRoleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFormFields(newVal));
        statusLabel.setText("");
        statusLabel.setWrapText(true);
        updateFormFields(null);
    }

    // Updated method to handle visibility
    private void updateFormFields(String role) {
        boolean showParent = "Parent".equals(role);
        boolean showDoctor = "Doctor".equals(role);
        boolean showSupporter = "Supporter".equals(role);
        if (parentFields != null) {
            parentFields.setVisible(showParent);
            parentFields.setManaged(showParent);
        }
        if (doctorFields != null) {
            doctorFields.setVisible(showDoctor);
            doctorFields.setManaged(showDoctor);
        }
        if (supporterFields != null) {
            supporterFields.setVisible(showSupporter);
            supporterFields.setManaged(showSupporter);
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {
        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: red;");

        // --- Validation ---
        String role = userRoleComboBox.getValue();
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = passwordField1.getText();
        String confirmPassword = passwordField2.getText();

        // Basic fields check
        if (isNullOrEmpty(role) || isNullOrEmpty(name) || isNullOrEmpty(email) || isNullOrEmpty(phone) || isNullOrEmpty(password) || aadhaarFile == null) {
            showError("Please fill common fields (Role, Name, Email, Phone, Password) and upload Aadhaar."); return;
        }
        // Format/Length checks
        if (!phone.matches("\\d{10}")) { showError("Enter a valid 10-digit phone number."); return; }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) { showError("Enter a valid email address."); return; }
        if (password.length() < 6) { showError("Password must be at least 6 characters."); return; }
        if (!password.equals(confirmPassword)) { showError("Passwords do not match."); passwordField1.clear(); passwordField2.clear(); return; }

        // Role-specific validation
        if ("Parent".equals(role)) {
            if (isNullOrEmpty(childNameField.getText()) || childDobPicker.getValue() == null) {
                showError("Child's Name and Date of Birth are required for Parent."); return;
            }
            // Optional fields (medicalReportFile, disabilityReportFile) don't need validation here
        } else if ("Doctor".equals(role)) {
            if (isNullOrEmpty(specializationField.getText()) || isNullOrEmpty(licenseNoField.getText()) || licenseFile == null) {
                showError("Specialization, License No., and License File are required for Doctor."); return;
            }
        }
        // Firebase connection check
        if (!ThanalApp.isFirebaseInitialized()) { showError("Backend connection error. Cannot register."); return; }
        // --- End Validation ---

        try {
            // --- Prepare Data ---
            Map<String, Object> userData = new HashMap<>();
            String roleLower = role.toLowerCase();
            userData.put("role", roleLower);
            userData.put("name", name);
            userData.put("email", email);
            userData.put("phone", phone);
            userData.put("password", password); // !! DEMO ONLY - INSECURE !!
            userData.put("status", "pending");
            userData.put("registeredAt", Timestamp.now());

            // --- Handle Files ---
            String aadhaarPath = saveFileLocally(aadhaarFile, email, "aadhaar");
            if (aadhaarPath == null) return; // Error shown in helper
            userData.put("aadhaarFilePath", aadhaarPath);

            // --- Role Specific Data ---
            if ("parent".equals(roleLower)) {
                userData.put("childName", childNameField.getText());
                userData.put("childDob", childDobPicker.getValue().toString()); // Save DatePicker value as String
                if (medicalReportFile != null) {
                    String path = saveFileLocally(medicalReportFile, email, "medical"); if (path == null) return;
                    userData.put("medicalReportPath", path);
                }
                if (disabilityReportFile != null) {
                    String path = saveFileLocally(disabilityReportFile, email, "disability"); if (path == null) return;
                    userData.put("disabilityReportPath", path);
                }
            } else if ("doctor".equals(roleLower)) {
                userData.put("specialization", specializationField.getText());
                userData.put("licenseNo", licenseNoField.getText());
                String path = saveFileLocally(licenseFile, email, "license"); if (path == null) return;
                userData.put("licenseFilePath", path);
            } else if ("supporter".equals(roleLower)) {
                userData.put("organization", organizationField.getText()); // Save organization
            }

            // --- Save to Firestore ---
            Firestore db = FirestoreClient.getFirestore();
            // Use email as the document ID in 'pending_users'
            ApiFuture<WriteResult> future = db.collection("pending_users").document(email).set(userData);

            future.get(); // Wait for completion (blocks UI - consider background task for production)
            System.out.println("Pending registration saved for: " + email);

            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Registration request submitted successfully! Waiting for admin approval.");
            showAlert(Alert.AlertType.INFORMATION, "Request Submitted", "Your registration request is pending admin approval. You will be able to log in once approved.");
            clearFormFields();

        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper to save file locally
    private String saveFileLocally(File file, String userEmail, String fileType) {
        if (file == null) return null;
        try {
            // Sanitize email to create a safe directory name
            String userSubDir = userEmail.replaceAll("[^a-zA-Z0-9.-]", "_");
            Path userDirPath = Paths.get(LOCAL_UPLOAD_DIR, userSubDir);
            // Create directory if it doesn't exist
            if (!Files.exists(userDirPath)) Files.createDirectories(userDirPath);

            // Create a unique filename
            String extension = ""; String name = file.getName();
            int i = name.lastIndexOf('.');
            if (i > 0 && i < name.length() - 1) extension = name.substring(i);
            String safeFileType = fileType.replaceAll("[^a-zA-Z0-9_-]", "");
            String uniqueFileName = safeFileType + "_" + System.currentTimeMillis() + extension;
            Path destinationPath = userDirPath.resolve(uniqueFileName);

            // Copy the file
            Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Saved file: " + destinationPath);
            return destinationPath.toString(); // Return the path where it was saved
        } catch (IOException | SecurityException e) {
            showError("Error saving file [" + fileType + "]: " + e.getMessage() + "\nCheck permissions for " + LOCAL_UPLOAD_DIR);
            e.printStackTrace();
            return null; // Indicate failure
        }
    }

    // --- File Choosers & UI Helpers ---
    @FXML void handleAadhaarUpload(ActionEvent event) { aadhaarFile = chooseFile("Select Aadhaar ID", event); updateFileLabel(aadhaarLabel, aadhaarFile); }
    @FXML void handleMedicalReportUpload(ActionEvent event) { medicalReportFile = chooseFile("Select Medical Report (PDF)", event); updateFileLabel(medicalReportLabel, medicalReportFile); }
    @FXML void handleDisabilityReportUpload(ActionEvent event) { disabilityReportFile = chooseFile("Select Disability Report (PDF)", event); updateFileLabel(disabilityReportLabel, disabilityReportFile); }
    @FXML void handleLicenseUpload(ActionEvent event) { licenseFile = chooseFile("Select Doctor License (Image or PDF)", event); updateFileLabel(licenseLabel, licenseFile); }

    // Helper to show FileChooser dialog
    private File chooseFile(String title, ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (title.contains("(PDF)")) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        } else if (title.contains("(Image or PDF)")) {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images (*.png, *.jpg)", "*.png", "*.jpg", "*.jpeg"),
                    new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
        } else { // Default for Aadhaar or unspecified
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images (*.png, *.jpg)", "*.png", "*.jpg", "*.jpeg"),
                    new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"),
                    new FileChooser.ExtensionFilter("All Files", "*.*") );
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (stage == null) { showError("Cannot open file chooser (Stage not found)."); return null; }
        return fileChooser.showOpenDialog(stage);
    }

    private void updateFileLabel(Label label, File file) {
        if (label != null) {
            label.setText(file != null ? file.getName() : "No file selected.");
        }
    }

    // Show error message in status label and alert dialog
    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setText(message);
        // Also show a popup for clarity
        showAlert(Alert.AlertType.WARNING, "Input Error", message);
    }

    // Navigate back to the login screen
    @FXML void handleBackButtonAction(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "login.fxml");
    }

    // Centralized Alert dialog helper
    private void showAlert(Alert.AlertType type, String title, String msg) {
        // Ensure UI updates happen on the JavaFX Application Thread
        Platform.runLater(() -> new Alert(type, msg, ButtonType.OK){{
            setTitle(title);
            setHeaderText(null); // No header text
        }}.showAndWait());
    }

    private boolean isNullOrEmpty(String s){ return s == null || s.trim().isEmpty(); }

    // Method to clear all form fields after successful registration
    private void clearFormFields() {
        userRoleComboBox.getSelectionModel().clearSelection();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        passwordField1.clear();
        passwordField2.clear();
        statusLabel.setText(""); // Clear status

        // Reset file labels and variables
        updateFileLabel(aadhaarLabel, null); aadhaarFile = null;
        updateFileLabel(medicalReportLabel, null); medicalReportFile = null;
        updateFileLabel(disabilityReportLabel, null); disabilityReportFile = null;
        updateFileLabel(licenseLabel, null); licenseFile = null;

        // Clear role-specific fields
        childNameField.clear();
        childDobPicker.setValue(null);
        specializationField.clear();
        licenseNoField.clear();
        organizationField.clear();

        // Hide role-specific sections
        updateFormFields(null);
    }
}