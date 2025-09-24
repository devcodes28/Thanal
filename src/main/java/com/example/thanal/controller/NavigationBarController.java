package com.example.thanal.controller;

import com.example.thanal.util.SceneSwitcher;
import javafx.event.ActionEvent;
import java.io.IOException;

public class NavigationBarController {

    public void goToHome(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "home-page.fxml", true);
    }

    public void goToAboutUs(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "about-us.fxml", true);
    }

    public void goToContactUs(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "contact-us.fxml", true);
    }

    public void goToLogin(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "login.fxml", false);
    }
}