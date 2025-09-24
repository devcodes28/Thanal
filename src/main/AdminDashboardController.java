package com.example.thanal.controller;

import com.example.thanal.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import java.io.IOException;

public class AdminDashboardController {

    @FXML private ListView<String> userRequestsListView;
    @FXML private ListView<String> allUsersListView;

    @FXML
    public void initialize() {
        userRequestsListView.getItems().addAll("New Parent: parent_new@thanal.com", "New Doctor: doc_new@thanal.com");
        allUsersListView.getItems().addAll("parent@thanal.com", "doctor@thanal.com", "supporter@thanal.com");
    }

    @FXML void approveUser() { System.out.println("Approved: " + userRequestsListView.getSelectionModel().getSelectedItem()); }
    @FXML void deleteUser() { System.out.println("Deleted: " + allUsersListView.getSelectionModel().getSelectedItem());}
    @FXML void handleLogout(ActionEvent event) throws IOException { SceneSwitcher.switchScene(event, "home-page.fxml", true); }
}