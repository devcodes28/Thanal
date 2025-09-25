package com.example.thanal.controller;

import com.example.thanal.model.*;
import com.example.thanal.util.SceneSwitcher;
import com.example.thanal.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.Arrays;

public class ParentDashboardController {

    // These are now typed with our Model classes, not String
    @FXML private ListView<Blog> blogListView;
    @FXML private ListView<QnQuestion> qnaListView;
    @FXML private ListView<Doctor> doctorListView;

    @FXML private Label welcomeLabel; // We'll add a welcome label to the FXML

    // Other FXML fields remain
    @FXML private TextArea blogContentView;
    @FXML private TextField commentField;
    @FXML private TextArea questionArea;
    @FXML private TextField behaviourField;
    @FXML private TextField triggersField;

    private Parent currentUser;

    @FXML
    public void initialize() {
        // 1. Get the logged-in user from the session
        User user = SessionManager.getInstance().getCurrentUser();
        if (user instanceof Parent) {
            this.currentUser = (Parent) user;
            welcomeLabel.setText("Parent Dashboard: " + currentUser.getName());
        } else {
            // Handle error: a non-parent accessed this dashboard
            welcomeLabel.setText("Parent Dashboard: Error");
            return;
        }

        // 2. Populate ListViews with Model objects
        setupBlogListView();
        setupQnaListView();
        setupDoctorListView();
    }

    private void setupBlogListView() {
        // Mock data
        Blog b1 = new Blog(); b1.setTitle("Understanding Stimming"); b1.setContent("Stimming is repetitive behavior...");
        Blog b2 = new Blog(); b2.setTitle("The Importance of Routine"); b2.setContent("Routines provide structure...");
        blogListView.getItems().addAll(b1, b2);

        // 3. Customize how each 'Blog' object is displayed in the list
        blogListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Blog item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getTitle() == null) {
                    setText(null);
                } else {
                    setText(item.getTitle()); // Display the blog's title
                }
            }
        });

        // Add a listener to show content when a blog is selected
        blogListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        blogContentView.setText(newVal.getContent());
                    }
                });
    }

    private void setupQnaListView() {
        QnQuestion q1 = new QnQuestion(); q1.setTitle("How to handle meltdowns?");
        QnQuestion q2 = new QnQuestion(); q2.setTitle("Best educational toys?");
        qnaListView.getItems().addAll(q1, q2);

        qnaListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(QnQuestion item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });
    }

    private void setupDoctorListView() {
        Doctor d1 = new Doctor(); d1.setName("Dr. Smith"); d1.setSpecialization("Pediatric Neurologist");
        Doctor d2 = new Doctor(); d2.setName("Dr. Jones"); d2.setSpecialization("Child Psychologist");
        doctorListView.getItems().addAll(d1, d2);

        doctorListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Doctor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - " + item.getSpecialization());
                }
            }
        });
    }

    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        SessionManager.getInstance().clearSession();
        SceneSwitcher.switchScene(event, "home-page.fxml");
    }

    @FXML void requestConsultation() {
        Doctor selectedDoctor = doctorListView.getSelectionModel().getSelectedItem();
        if (selectedDoctor != null) {
            System.out.println(currentUser.getName() + " is requesting consultation with " + selectedDoctor.getName());
        } else {
            System.out.println("No doctor selected.");
        }
    }

    // ... other methods like postComment(), submitQuestion() etc. ...
}