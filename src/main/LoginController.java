package com.example.thanal.controller;

import com.example.thanal.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
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
            statusLabel.setText("Login Successful");
            SceneSwitcher.switchScene(event, role + "-dashboard.fxml", true);
        } else {
            statusLabel.setText("Invalid credentials. Please try again.");
        }
    }

    @FXML
    void handleBackButtonAction(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "home-page.fxml", true);
    }

    private String authenticate(String email, String password) {
        if ("parent@thanal.com".equals(email) && "pass".equals(password)) return "parent";
        if ("doctor@thanal.com".equals(email) && "pass".equals(password)) return "doctor";
        if ("supporter@thanal.com".equals(email) && "pass".equals(password)) return "supporter";
        if ("admin@thanal.com".equals(email) && "pass".equals(password)) return "admin";
        return null;
    }
}