package com.example.thanal.controller;

import com.example.thanal.model.*;
import com.example.thanal.util.SceneSwitcher;
import com.example.thanal.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;

public class DoctorDashboardController {

    // FXML fields typed with Model classes
    @FXML private ListView<Blog> myBlogsListView;
    @FXML private ListView<Consultation> consultationRequestsListView;
    @FXML private ListView<QnQuestion> unansweredQuestionsListView;
    @FXML private ListView<Parent> approvedPatientsListView; // Represents parents who approved data sharing

    @FXML private Label welcomeLabel;
    @FXML private TextArea answerArea;

    private Doctor currentUser;

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user instanceof Doctor) {
            this.currentUser = (Doctor) user;
            welcomeLabel.setText("Doctor Dashboard: " + currentUser.getName());
        } else {
            welcomeLabel.setText("Doctor Dashboard: Error");
            return;
        }

        setupBlogs();
        setupConsultations();
        setupQuestions();
        setupApprovedPatients();
    }

    private void setupBlogs() {
        // Mock data for blogs written by this doctor
        Blog b1 = new Blog(); b1.setTitle("My Article on Sensory Integration");
        myBlogsListView.getItems().add(b1);
        myBlogsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Blog item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });
    }

    private void setupConsultations() {
        // Mock data for consultation requests
        Parent p1 = new Parent(); p1.setName("Sumita"); p1.setChildName("Shankar");
        Consultation c1 = new Consultation(); c1.setParentId(p1.getUserId()); c1.setStatus("REQUESTED");
        // In a real app, you'd link the parent object itself

        consultationRequestsListView.getItems().add(c1);
        consultationRequestsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Consultation item, boolean empty) {
                super.updateItem(item, empty);
                // In a real app, you'd fetch the Parent's name from the parentId
                setText(empty || item == null ? null : "Request from Parent for Consultation");
            }
        });
    }

    private void setupQuestions() {
        QnQuestion q1 = new QnQuestion(); q1.setTitle("How to handle meltdowns?");
        unansweredQuestionsListView.getItems().add(q1);
        unansweredQuestionsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(QnQuestion item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });
    }

    private void setupApprovedPatients() {
        Parent p1 = new Parent(); p1.setName("Sumita"); p1.setChildName("Shankar");
        approvedPatientsListView.getItems().add(p1);
        approvedPatientsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Parent item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Patient: " + item.getChildName() + " (Parent: " + item.getName() + ")");
                }
            }
        });
    }

    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        SessionManager.getInstance().clearSession();
        SceneSwitcher.switchScene(event, "home-page.fxml");
    }

    // --- Action Handlers now work with Objects ---
    @FXML void acceptConsultation() {
        Consultation selected = consultationRequestsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("ACCEPTED");
            System.out.println("Accepted consultation request.");
            consultationRequestsListView.refresh(); // Update the view
        }
    }

    @FXML
    void declineConsultation() {
        Consultation selected = consultationRequestsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            consultationRequestsListView.getItems().remove(selected);
            System.out.println("Declined consultation request.");
        }
    }


    @FXML void submitAnswer() {
        QnQuestion selected = unansweredQuestionsListView.getSelectionModel().getSelectedItem();
        if (selected != null && !answerArea.getText().isEmpty()) {
            QnAnswer answer = new QnAnswer();
            answer.setAuthorId(currentUser.getUserId());
            answer.setQuestionId(selected.getQuestionId());
            answer.setContent(answerArea.getText());
            System.out.println("Answer submitted for: " + selected.getTitle());
            answerArea.clear();
        }
    }

    @FXML
    void viewPatientReport() {
        Parent selected = approvedPatientsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Viewing report for patient of: " + selected.getName());
        }
    }

    /**
     * ADDED THIS METHOD TO FIX THE ERROR.
     * This method is called when the "Write New Blog" button is clicked.
     */
    @FXML
    void writeNewBlog(ActionEvent event) {
        // Logic to open a new window or switch to a blog editor scene would go here.
        System.out.println("Navigating to the new blog editor...");
    }
}