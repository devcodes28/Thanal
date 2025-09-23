package com.example.thanal.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SupporterDashboardController {

    @FXML private ListView<String> blogListView;
    @FXML private TextArea blogContentView;
    @FXML private TextField blogRatingField;
    @FXML private TextArea recommendationArea;

    @FXML
    public void initialize() {
        blogListView.getItems().addAll("Understanding Autism Stimming", "The Importance of Routine");
    }

    @FXML void submitRating() { System.out.println("Rating submitted: " + blogRatingField.getText()); }
    @FXML void submitRecommendation() { System.out.println("Recommendation submitted: " + recommendationArea.getText()); }
}
