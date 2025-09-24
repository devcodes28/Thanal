package com.example.thanal.controller;

import com.example.thanal.util.SceneSwitcher;
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
        String role = authenticate(email, password);

        if (role != null) {
            statusLabel.setText("Login Successful!");
            SceneSwitcher.switchScene(event, role + "-dashboard.fxml");
        } else {
            statusLabel.setText("Invalid credentials. Please try again.");
        }
    }

    @FXML void handleBackButtonAction(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "home-page.fxml");
    }

    private String authenticate(String email, String password) {
        if ("parent@thanal.com".equals(email) && "pass".equals(password)) return "parent";
        if ("doctor@thanal.com".equals(email) && "pass".equals(password)) return "doctor";
        if ("supporter@thanal.com".equals(email) && "pass".equals(password)) return "supporter";
        if ("admin@thanal.com".equals(email) && "pass".equals(password)) return "admin";
        return null;
    }

    // --- NEW ACTION HANDLERS FOR THE HYPERLINKS ---

    @FXML
    void handleForgotPassword(ActionEvent event) {
        System.out.println("Forgot Password link clicked!");
        // You can add logic here to show a password reset screen
    }

    @FXML
    void handleRegisterParent(ActionEvent event) {
        System.out.println("Register as Parent link clicked!");
        // You can add logic here to navigate to a parent registration page
    }

    @FXML
    void handleRegisterDoctor(ActionEvent event) {
        System.out.println("Register as Doctor link clicked!");
        // You can add logic here to navigate to a doctor registration page
    }

    @FXML
    void handleRegisterSupporter(ActionEvent event) {
        System.out.println("Register as Supporter link clicked!");
        // You can add logic here to navigate to a supporter registration page
    }
}