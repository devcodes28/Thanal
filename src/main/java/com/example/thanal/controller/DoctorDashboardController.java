package com.example.thanal.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class DoctorDashboardController {

    @FXML private ListView<String> myBlogsListView;
    @FXML private ListView<String> consultationRequestsListView;
    @FXML private ListView<String> unansweredQuestionsListView;
    @FXML private TextArea answerArea;
    @FXML private ListView<String> approvedPatientsListView;

    @FXML
    public void initialize() {
        // Populate with dummy data
        myBlogsListView.getItems().add("My article on sensory integration therapy");
        consultationRequestsListView.getItems().add("Request from Parent A for Child X");
        unansweredQuestionsListView.getItems().add("Q: How to handle meltdowns?");
        approvedPatientsListView.getItems().add("Patient: Child X (Access Approved)");
    }

    @FXML void writeNewBlog() { System.out.println("Opening blog editor...");}
    @FXML void acceptConsultation() { System.out.println("Accepted: " + consultationRequestsListView.getSelectionModel().getSelectedItem());}
    @FXML void declineConsultation() { System.out.println("Declined: " + consultationRequestsListView.getSelectionModel().getSelectedItem());}
    @FXML void submitAnswer() { System.out.println("Answer submitted for: " + unansweredQuestionsListView.getSelectionModel().getSelectedItem());}
    @FXML void viewPatientReport() { System.out.println("Viewing report for: " + approvedPatientsListView.getSelectionModel().getSelectedItem());}
}
