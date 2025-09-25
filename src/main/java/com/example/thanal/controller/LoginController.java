package com.example.thanal.controller;

import com.example.thanal.model.*;
import com.example.thanal.util.SceneSwitcher;
import com.example.thanal.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> userRoleComboBox; // Added ComboBox

    /**
     * This method is automatically called after the FXML file has been loaded.
     * We use it to populate the ComboBox.
     */
    @FXML
    public void initialize() {
        // Add the different user roles to the dropdown
        userRoleComboBox.getItems().addAll("Parent", "Doctor", "Supporter", "Admin");
    }

    @FXML
    void handleLoginButtonAction(ActionEvent event) throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = userRoleComboBox.getValue(); // Get selected role

        // Validity Check for all fields
        if (role == null || role.isEmpty()) {
            statusLabel.setText("Please select a user role.");
            return;
        }
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Email and password cannot be empty.");
            return;
        }

        User loggedInUser = authenticate(email, password, role);

        if (loggedInUser != null) {
            // Store the logged-in user in the session
            SessionManager.getInstance().setCurrentUser(loggedInUser);

            statusLabel.setText("Login Successful!");
            // The dashboard file name is derived from the role (e.g., "parent-dashboard.fxml")
            SceneSwitcher.switchScene(event, loggedInUser.getRole() + "-dashboard.fxml");
        } else {
            statusLabel.setText("Invalid credentials for the selected role.");
        }
    }

    /**
     * Updated authenticate method to also check the role.
     */
    private User authenticate(String email, String password, String role) {
        // This is our mock authentication.
        if ("Parent".equals(role) && "parent@thanal.com".equals(email) && "pass".equals(password)) {
            Parent p = new Parent();
            p.setUserId(1L); p.setName("Parent"); p.setEmail(email); p.setRole("parent");
            p.setChildName("Leo Carter");
            return p;
        }
        if ("Doctor".equals(role) && "doctor@thanal.com".equals(email) && "pass".equals(password)) {
            Doctor d = new Doctor();
            d.setUserId(2L); d.setName("Dr Gupta"); d.setEmail(email); d.setRole("doctor");
            d.setSpecialization("Pediatric Neurology");
            return d;
        }
        if ("Supporter".equals(role) && "supporter@thanal.com".equals(email) && "pass".equals(password)) {
            Supporter s = new Supporter();
            s.setUserId(3L); s.setName("Community Supporter"); s.setEmail(email); s.setRole("supporter");
            s.setOrganization("Autism Advocates");
            return s;
        }
        if ("Admin".equals(role) && "admin@thanal.com".equals(email) && "pass".equals(password)) {
            Admin a = new Admin();
            a.setUserId(4L); a.setName("Admin User"); a.setEmail(email); a.setRole("admin");
            return a;
        }
        return null;
    }

    // --- Other methods remain the same ---
    @FXML void handleBackButtonAction(ActionEvent event) throws IOException { SceneSwitcher.switchScene(event, "home-page.fxml"); }
    @FXML void handleForgotPassword(ActionEvent event) { System.out.println("Forgot Password link clicked!"); }
    @FXML void handleRegisterParent(ActionEvent event) { System.out.println("Register as Parent link clicked!"); }
    @FXML void handleRegisterDoctor(ActionEvent event) { System.out.println("Register as Doctor link clicked!"); }
    @FXML void handleRegisterSupporter(ActionEvent event) { System.out.println("Register as Supporter link clicked!"); }
}