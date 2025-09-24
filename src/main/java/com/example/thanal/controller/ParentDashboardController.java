package com.example.thanal.controller;

import com.example.thanal.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;

public class ParentDashboardController {

    @FXML private ListView<String> blogListView;
    @FXML private TextArea blogContentView;
    @FXML private TextField blogRatingField;
    @FXML private TextField commentField;
    @FXML private ListView<String> qnaListView;
    @FXML private TextArea questionArea;
    @FXML private TextField behaviourField;
    @FXML private TextField triggersField;
    @FXML private TextField aidAppName;
    @FXML private TextArea aidAppDetails;
    @FXML private ListView<String> doctorListView;

    @FXML
    public void initialize() {
        blogListView.getItems().addAll("Understanding Autism Stimming", "The Importance of Routine");
        qnaListView.getItems().addAll("Q: How to handle meltdowns?", "Q: Best educational toys?");
        doctorListView.getItems().addAll("Dr. Smith - Pediatric Neurologist", "Dr. Jones - Child Psychologist");
    }

    @FXML void postComment() { System.out.println("Commented: " + commentField.getText()); }
    @FXML void writeNewBlog() { System.out.println("Opening new blog editor..."); }
    @FXML void submitQuestion() { System.out.println("Question Submitted: " + questionArea.getText()); }
    @FXML void logBehaviour() { System.out.println("Behaviour logged: " + behaviourField.getText()); }
    @FXML void downloadReport() { System.out.println("Downloading behavior report..."); }
    @FXML void submitAidApplication() { System.out.println("Financial aid application submitted for " + aidAppName.getText()); }
    @FXML void requestConsultation() { System.out.println("Consultation requested with " + doctorListView.getSelectionModel().getSelectedItem()); }

    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        // CORRECTED: Removed the boolean argument
        SceneSwitcher.switchScene(event, "home-page.fxml");
    }
}