package com.example.thanal.controller;
import com.example.thanal.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;

public class SupporterDashboardController {
    @FXML private ListView<String> blogListView;
    @FXML private TextArea blogContentView;
    @FXML private TextField blogRatingField;
    @FXML private TextArea recommendationArea;

    @FXML
    public void initialize() {
        blogListView.getItems().addAll("Understanding Autism Stimming", "The Importance of Routine");
    }

    @FXML
    void submitRating() {
        String rating = blogRatingField.getText();
        if (rating != null && !rating.trim().isEmpty()) {
            try {
                int rate = Integer.parseInt(rating);
                if (rate >= 1 && rate <= 5) {
                    System.out.println("Rating submitted: " + rating);
                    blogRatingField.clear();
                } else {
                    System.out.println("Invalid rating. Please enter a number between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    @FXML
    void submitRecommendation() {
        String recommendation = recommendationArea.getText();
        if (recommendation != null && !recommendation.trim().isEmpty()) {
            System.out.println("Recommendation submitted: " + recommendation);
            recommendationArea.clear();
        }
    }

    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "home-page.fxml");
    }
}