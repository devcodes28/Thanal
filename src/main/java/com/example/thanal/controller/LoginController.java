package com.example.thanal.controller;

import com.example.thanal.model.*;
import com.example.thanal.util.SceneSwitcher;
import com.example.thanal.util.SessionManager; // <-- Import SessionManager
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    void handleLoginButtonAction(ActionEvent event) throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();
        User loggedInUser = authenticate(email, password);

        if (loggedInUser != null) {
            // Store the logged-in user in the session
            SessionManager.getInstance().setCurrentUser(loggedInUser);

            statusLabel.setText("Login Successful!");
            SceneSwitcher.switchScene(event, loggedInUser.getRole() + "-dashboard.fxml");
        } else {
            statusLabel.setText("Invalid credentials. Please try again.");
        }
    }

    private User authenticate(String email, String password) {
        // This is our mock authentication. In a real app, this comes from a database.
        if ("parent@thanal.com".equals(email) && "pass".equals(password)) {
            Parent p = new Parent();
            p.setUserId(1L); p.setName("Emily Carter"); p.setEmail(email); p.setRole("parent");
            p.setChildName("Leo Carter");
            return p;
        }
        if ("doctor@thanal.com".equals(email) && "pass".equals(password)) {
            Doctor d = new Doctor();
            d.setUserId(2L); d.setName("Dr. Aris Thorne"); d.setEmail(email); d.setRole("doctor");
            d.setSpecialization("Pediatric Neurology");
            return d;
        }
        if ("supporter@thanal.com".equals(email) && "pass".equals(password)) {
            Supporter s = new Supporter();
            s.setUserId(3L); s.setName("Alex Chen"); s.setEmail(email); s.setRole("supporter");
            s.setOrganization("Autism Advocates");
            return s;
        }
        if ("admin@thanal.com".equals(email) && "pass".equals(password)) {
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