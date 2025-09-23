package com.example.thanal.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Button registerButton;

    @FXML
    void handleLoginButtonAction(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Dummy authentication logic
        String role = authenticate(email, password);

        if (role != null) {
            statusLabel.setText("Login Successful");
            loadDashboard(role, event);
        } else {
            statusLabel.setText("Invalid credentials");
        }
    }

    @FXML
    void handleRegisterButtonAction(ActionEvent event) {
        // Logic to open a registration window
        System.out.println("Register button clicked");
    }

    private String authenticate(String email, String password) {
        // In a real application, you would query a database
        if ("parent@thanal.com".equals(email) && "pass".equals(password)) {
            return "parent";
        } else if ("doctor@thanal.com".equals(email) && "pass".equals(password)) {
            return "doctor";
        } else if ("supporter@thanal.com".equals(email) && "pass".equals(password)) {
            return "supporter";
        } else if ("admin@thanal.com".equals(email) && "pass".equals(password)) {
            return "admin";
        }
        return null;
    }

    private void loadDashboard(String role, ActionEvent event) {
        try {
            // Corrected path from previous step
            String fxmlFile = "/com/example/thanal/" + role + "-dashboard.fxml";
            Parent dashboard = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(dashboard);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setResizable(true);
            window.centerOnScreen();
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error loading dashboard.");
        }
    }
}