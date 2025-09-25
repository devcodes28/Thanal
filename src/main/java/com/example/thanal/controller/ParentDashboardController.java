package com.example.thanal.controller;

import com.example.thanal.model.*;
import com.example.thanal.util.SceneSwitcher;
import com.example.thanal.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ParentDashboardController {

    // --- FXML UI Elements ---
    @FXML private Label welcomeLabel;
    @FXML private TextArea blogContentView;
    @FXML private TextField commentField;
    @FXML private TextArea questionArea;
    @FXML private TextField behaviourField;
    @FXML private TextField triggersField;
    @FXML private ListView<Blog> blogListView;
    @FXML private ListView<QnQuestion> qnaListView;
    @FXML private ListView<Doctor> doctorListView;
    @FXML private ListView<BehaviorLog> behaviorLogListView;
    @FXML private ListView<FinancialApplication> financialAidListView;
    @FXML private ComboBox<String> govtDeptComboBox;

    // --- Class Variables ---
    private Parent currentUser;
    private final ObservableList<BehaviorLog> behaviorLogs = FXCollections.observableArrayList();
    private final ObservableList<FinancialApplication> financialApplications = FXCollections.observableArrayList();

    /**
     * This method is called by JavaFX after the FXML file has been loaded.
     * It's the central place to set up the initial state of the dashboard.
     */
    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user instanceof Parent) {
            this.currentUser = (Parent) user;
            welcomeLabel.setText("Welcome, " + currentUser.getName());
        } else {
            welcomeLabel.setText("Parent Dashboard");
        }

        // Populate all the UI sections with data and cell factories
        setupBlogListView();
        setupQnaListView();
        setupDoctorListView();
        setupBehaviorTracking();
        setupFinancialAid();
    }

    // --- SETUP METHODS ---

    private void setupBlogListView() {
        Blog b1 = new Blog(); b1.setTitle("Understanding Stimming: A Parent's Guide"); b1.setContent("Stimming is repetitive behavior...");
        Blog b2 = new Blog(); b2.setTitle("The Importance of a Daily Routine"); b2.setContent("Routines provide structure...");
        blogListView.getItems().addAll(b1, b2);
        blogListView.setCellFactory(param -> new ListCell<Blog>() {
            @Override
            protected void updateItem(Blog item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });
        blogListView.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null) blogContentView.setText(val.getContent());
        });
    }

    private void setupQnaListView() {
        QnQuestion q1 = new QnQuestion(); q1.setTitle("How to handle meltdowns in public?");
        qnaListView.getItems().add(q1);
        qnaListView.setCellFactory(param -> new ListCell<QnQuestion>() {
            @Override
            protected void updateItem(QnQuestion item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });
    }

    private void setupDoctorListView() {
        Doctor d1 = new Doctor(); d1.setName("Dr. Anjali Rao"); d1.setSpecialization("Pediatric Neurologist");
        doctorListView.getItems().add(d1);
        doctorListView.setCellFactory(param -> new ListCell<Doctor>() {
            @Override
            protected void updateItem(Doctor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " - " + item.getSpecialization());
            }
        });
    }

    private void setupBehaviorTracking() {
        behaviorLogListView.setItems(behaviorLogs);
        behaviorLogListView.setCellFactory(param -> new ListCell<BehaviorLog>() {
            @Override
            protected void updateItem(BehaviorLog item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String date = item.getDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
                    setText(date + ": " + item.getBehaviors());
                }
            }
        });
    }

    private void setupFinancialAid() {
        govtDeptComboBox.getItems().addAll("National Trust Scheme", "State Disability Fund", "Health Ministry Grant");
        financialAidListView.setItems(financialApplications);
        financialAidListView.setCellFactory(param -> new ListCell<FinancialApplication>() {
            @Override
            protected void updateItem(FinancialApplication item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "To: " + item.getGovtDepartment() + " | Status: " + item.getStatus());
            }
        });
    }

    // --- ACTION HANDLERS (with robust feedback) ---

    @FXML
    void submitBehaviorLog() {
        String behavior = behaviourField.getText();
        if (behavior == null || behavior.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Behavior field cannot be empty.");
            return;
        }
        BehaviorLog log = new BehaviorLog();
        log.setParentId(currentUser.getUserId());
        log.setDate(LocalDate.now());
        log.setBehaviors(behavior);
        log.setTriggers(triggersField.getText());
        behaviorLogs.add(0, log);
        behaviourField.clear();
        triggersField.clear();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Behavior log has been saved successfully!");
    }

    @FXML
    void submitFinancialAid() {
        String department = govtDeptComboBox.getValue();
        if (department == null || department.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a department or scheme.");
            return;
        }
        FinancialApplication app = new FinancialApplication();
        app.setParentId(currentUser.getUserId());
        app.setGovtDepartment(department);
        app.setStatus("Submitted");
        financialApplications.add(app);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Your application to " + department + " has been submitted.");
    }

    @FXML
    void submitQuestion() {
        if (questionArea.getText() != null && !questionArea.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Your question has been posted to the Q&A Forum.");
            questionArea.clear();
        } else {
            showAlert(Alert.AlertType.WARNING, "Input Error", "The question field cannot be empty.");
        }
    }

    @FXML
    void requestConsultation() {
        Doctor selectedDoctor = doctorListView.getSelectionModel().getSelectedItem();
        if (selectedDoctor != null) {
            showAlert(Alert.AlertType.INFORMATION, "Request Sent", "Your consultation request has been sent to " + selectedDoctor.getName() + ".");
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a doctor from the list.");
        }
    }

    // --- GAME & LOGOUT HANDLERS ---
    @FXML void playEmotionGame() { showAlert(Alert.AlertType.INFORMATION, "Game Zone", "Launching the Emotion Match Game!"); }
    @FXML void playStoryGame() { showAlert(Alert.AlertType.INFORMATION, "Game Zone", "Launching the Story Builder Game!"); }
    @FXML void playPatternGame() { showAlert(Alert.AlertType.INFORMATION, "Game Zone", "Launching the Pattern Recognition Game!"); }

    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        SessionManager.getInstance().clearSession();
        SceneSwitcher.switchScene(event, "home-page.fxml");
    }

    // --- HELPER METHOD ---
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}