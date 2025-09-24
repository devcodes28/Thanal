package com.example.thanal.controller;

import com.example.thanal.util.SceneSwitcher;
import javafx.event.ActionEvent;
import java.io.IOException;

public class NavigationBarController {

    public void goToHome(ActionEvent event) throws IOException {
        // CORRECTED: Removed the boolean argument
        SceneSwitcher.switchScene(event, "home-page.fxml");
    }

    public void goToAboutUs(ActionEvent event) throws IOException {
        // CORRECTED: Removed the boolean argument
        SceneSwitcher.switchScene(event, "about-us.fxml");
    }

    public void goToContactUs(ActionEvent event) throws IOException {
        // CORRECTED: Removed the boolean argument
        SceneSwitcher.switchScene(event, "contact-us.fxml");
    }

    public void goToLogin(ActionEvent event) throws IOException {
        // CORRECTED: Removed the boolean argument
        SceneSwitcher.switchScene(event, "login.fxml");
    }
}