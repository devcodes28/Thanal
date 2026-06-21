package com.example.thanal.controller;

import com.example.thanal.main.ThanalApp; // Import ThanalApp
import com.example.thanal.model.Blog;
import com.example.thanal.model.User;
import com.example.thanal.util.SceneSwitcher;
import com.example.thanal.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

// --- FIREBASE IMPORTS ---
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp; // Import FirebaseApp
import com.google.firebase.cloud.FirestoreClient;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService; // Import ExecutorService
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// ---

import java.io.IOException;

public class SupporterDashboardController {

    @FXML private ListView<Blog> blogListView;
    @FXML private TextArea blogContentView;
    @FXML private TextField blogRatingField; // Basic rating, no aggregate calculation
    @FXML private TextArea recommendationArea;
    @FXML private Label welcomeLabel;

    private User currentUser;
    private final ObservableList<Blog> blogs = FXCollections.observableArrayList();
    private final ExecutorService firestoreExecutor = Executors.newSingleThreadExecutor();

    @FXML
    public void initialize() {
        if (!ThanalApp.isFirebaseInitialized()) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Firebase connection not ready.");
            if(welcomeLabel!=null) welcomeLabel.setText("Supporter Dashboard - OFFLINE");
            disableUIComponents();
            return;
        }

        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            if(welcomeLabel!=null) welcomeLabel.setText("Welcome, Supporter " + currentUser.getName());
            if (isNullOrEmpty(currentUser.getEmail())) {
                showAlert(Alert.AlertType.WARNING, "Session Error", "User email missing. Some functions might be limited.");
            }
        } else {
            if(welcomeLabel!=null) welcomeLabel.setText("Supporter Dashboard");
            showAlert(Alert.AlertType.WARNING, "Session Error", "Could not identify logged-in supporter.");
            disableUserActions();
        }

        blogListView.setItems(blogs);
        setupBlogList();
    }

    private void setupBlogList() {
        blogListView.setCellFactory(param -> new ListCell<Blog>() {
            @Override
            protected void updateItem(Blog item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });

        blogListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                blogContentView.setText(newVal.getContent());
                blogRatingField.setPromptText("Rate \"" + newVal.getTitle() + "\" (1-5)");
                blogRatingField.setDisable(currentUser == null);
            } else {
                blogContentView.clear();
                blogRatingField.setPromptText("Rate Blog (1-5)");
                blogRatingField.setDisable(true);
            }
            blogRatingField.clear();
        });
        loadBlogsFromFirestore();
    }

    private void loadBlogsFromFirestore() {
        blogs.clear();
        if (!ThanalApp.isFirebaseInitialized()) return;
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection("blogs")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(100)
                    .get();

            future.addListener(() -> {
                try {
                    List<Blog> loadedBlogs = new ArrayList<>();
                    for (QueryDocumentSnapshot document : future.get().getDocuments()) {
                        Blog blog = document.toObject(Blog.class);
                        loadedBlogs.add(blog);
                    }
                    Platform.runLater(() -> blogs.setAll(loadedBlogs));
                } catch (Exception e) { handleFirestoreError("load blogs", e); }
            }, firestoreExecutor);
        } catch (Exception e) { handleFirestoreError("initialize blog load", e); }
    }


    @FXML
    void submitRating() {
        Blog selectedBlog = blogListView.getSelectionModel().getSelectedItem();
        String ratingText = blogRatingField.getText();

        if (selectedBlog == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a blog to rate."); return;
        }
        if (isNullOrEmpty(ratingText)) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a rating (1-5)."); return;
        }
        if (!isCurrentUserValid()) return;

        try {
            int rating = Integer.parseInt(ratingText);
            if (rating >= 1 && rating <= 5) {
                System.out.println("Rating submitted: " + rating + " for blog: " + selectedBlog.getTitle());

                Map<String, Object> ratingData = new HashMap<>();
                ratingData.put("blogTitle", selectedBlog.getTitle());
                ratingData.put("rating", rating);
                ratingData.put("userId", currentUser.getUserId());
                ratingData.put("userEmail", currentUser.getEmail());
                ratingData.put("ratedAt", Timestamp.now());
                saveToFirestore("ratings", ratingData, "Rating submitted!", ()-> blogRatingField.clear());
            } else {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Invalid rating. Please enter a number between 1 and 5.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Invalid input. Please enter a number for the rating.");
        }
    }

    @FXML
    void submitRecommendation() {
        String recommendation = recommendationArea.getText();
        if (isNullOrEmpty(recommendation)) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Recommendation cannot be empty."); return;
        }
        if (!isCurrentUserValid()) return;
        Map<String, Object> recData = new HashMap<>();
        recData.put("recommendationText", recommendation);
        recData.put("submittedByEmail", currentUser.getEmail());
        recData.put("submittedByName", currentUser.getName());
        recData.put("submittedAt", Timestamp.now());
        saveToFirestore("recommendations", recData, "Recommendation submitted. Thank you!", ()-> recommendationArea.clear());
    }

    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        SessionManager.getInstance().clearSession();
        SceneSwitcher.switchScene(event, "home-page.fxml");
    }

    // --- Helper Methods ---
    private void saveToFirestore(String collection, Map<String, Object> data, String successMessage, Runnable onSuccessAction) {
        if (!ThanalApp.isFirebaseInitialized()) { handleFirestoreError("save " + collection + " (Firebase not init)", null); return; }
        try {
            Firestore db = FirestoreClient.getFirestore();
            // Use auto-generated ID
            ApiFuture<WriteResult> future = db.collection(collection).document().set(data);
            future.addListener(() -> {
                try {
                    future.get(); // check errors
                    System.out.println("Data saved to " + collection);
                    Platform.runLater(()->{
                        if(onSuccessAction!=null) onSuccessAction.run();
                        if(!isNullOrEmpty(successMessage)) showAlert(Alert.AlertType.INFORMATION, "Success", successMessage);
                    });
                } catch(Exception e){ handleFirestoreError("save " + collection + " result", e); }
            }, firestoreExecutor); // Use background executor
        } catch (Exception e) { handleFirestoreError("submit " + collection, e); }
    }

    private boolean isCurrentUserValid() {
        if (currentUser == null || isNullOrEmpty(currentUser.getEmail())) {
            showAlert(Alert.AlertType.ERROR, "Action Error", "Cannot perform action: user not logged in or email missing.");
            return false;
        }
        return true;
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (!Platform.isFxApplicationThread()) { Platform.runLater(() -> showActualAlert(alertType, title, message)); }
        else { showActualAlert(alertType, title, message); }
    }
    private void showActualAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait();
    }

    private void handleFirestoreError(String operation, Exception e) {
        String msg = (e != null) ? e.getMessage() : "Unknown"; System.err.println("Firestore error [" + operation + "]: " + msg); if(e != null) e.printStackTrace();
        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", "Operation [" + operation + "] failed."));
    }
    private boolean isNullOrEmpty(String str) { return str == null || str.trim().isEmpty(); }

    private void disableUIComponents(){
        if(blogListView!=null) blogListView.setDisable(true);
        if(blogContentView!=null) blogContentView.setDisable(true);
        if(blogRatingField!=null) blogRatingField.setDisable(true);
        if(recommendationArea!=null) recommendationArea.setDisable(true);
        // Find and disable submit buttons if needed
        System.out.println("Supporter dashboard UI components disabled.");
    }

    private void disableUserActions() {
        if(blogRatingField!=null) blogRatingField.setDisable(true);
        if(recommendationArea!=null) recommendationArea.setDisable(true);
    }
} // End of SupporterDashboardController class