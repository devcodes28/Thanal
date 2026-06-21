package com.example.thanal.controller;

import com.example.thanal.main.ThanalApp;
import com.example.thanal.model.*;
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
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
// ---

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// --- ADDED IMPORTS FOR VIEWING REPORTS ---
import java.awt.Desktop; // For opening files/folders
import java.io.File; // For file operations
import java.nio.file.Path; // For building paths
import java.nio.file.Paths; // For building paths
import java.nio.file.Files; // For checking if path exists
// ---

public class DoctorDashboardController {

    // --- FXML Fields ---
    @FXML private ListView<Blog> myBlogsListView;
    @FXML private ListView<Consultation> consultationRequestsListView;
    @FXML private ListView<QnQuestion> unansweredQuestionsListView;
    @FXML private ListView<Consultation> approvedPatientsListView;
    @FXML private Label welcomeLabel;
    @FXML private TextArea answerArea;

    // --- Class Variables ---
    private Doctor currentUser;
    private final ObservableList<Blog> blogs = FXCollections.observableArrayList();
    private final ObservableList<Consultation> consultations = FXCollections.observableArrayList();
    private final ObservableList<QnQuestion> questions = FXCollections.observableArrayList();
    private final ObservableList<Consultation> approvedPatients = FXCollections.observableArrayList();
    private final ExecutorService firestoreExecutor = Executors.newSingleThreadExecutor();

    // The base directory where files are stored, as defined in RegistrationController
    private static final String LOCAL_UPLOAD_DIR = "C:/Thanal_Uploads/";

    @FXML
    public void initialize() {
        if (!ThanalApp.isFirebaseInitialized()) { showAlert(Alert.AlertType.ERROR, "Init Error", "Firebase offline."); disableUIComponents(); return; }
        User user = SessionManager.getInstance().getCurrentUser();
        if (user instanceof Doctor) {
            this.currentUser = (Doctor) user;
            if (isNullOrEmpty(this.currentUser.getEmail())) { showAlert(Alert.AlertType.ERROR, "Login Error", "Doctor email missing."); disableUIComponents(); return; }
            welcomeLabel.setText("Dr. " + currentUser.getName());
        } else {
            welcomeLabel.setText("Doctor Dashboard: Error"); showAlert(Alert.AlertType.ERROR, "Login Error", "Not logged in as Doctor."); disableUIComponents(); return;
        }
        bindLists();
        setupUIComponents();
        loadAllData();
    }

    // --- UI Setup ---
    private void bindLists(){
        myBlogsListView.setItems(blogs); consultationRequestsListView.setItems(consultations);
        unansweredQuestionsListView.setItems(questions); approvedPatientsListView.setItems(approvedPatients);
    }
    private void setupUIComponents(){
        setupBlogs(); setupConsultations(); setupQuestions(); setupApprovedPatients();
    }

    private void loadAllData(){
        loadMyBlogs(); loadConsultationRequests(); loadUnansweredQuestions(); loadApprovedPatients();
    }

    private void setupBlogs() { myBlogsListView.setCellFactory(p->new ListCell<>(){ @Override protected void updateItem(Blog i, boolean e){super.updateItem(i,e);setText(e||i==null?null:i.getTitle());}}); }
    private void setupConsultations() { consultationRequestsListView.setCellFactory(p->new ListCell<>(){ @Override protected void updateItem(Consultation i, boolean e){super.updateItem(i,e);setText(e||i==null?null:"Request from "+i.getParentName());}}); }
    private void setupQuestions() { unansweredQuestionsListView.setCellFactory(p->new ListCell<>(){ @Override protected void updateItem(QnQuestion i, boolean e){super.updateItem(i,e);setText(e||i==null?null:i.getTitle());}}); unansweredQuestionsListView.getSelectionModel().selectedItemProperty().addListener((o,ov,nv)->answerArea.setPromptText(nv!=null?"Answer: \""+nv.getTitle()+"\"":"Select question...")); }

    private void setupApprovedPatients() {
        approvedPatientsListView.setCellFactory(p->new ListCell<>(){
            @Override protected void updateItem(Consultation i, boolean e){
                super.updateItem(i,e);
                // Show consent status
                String consent = (i != null && i.isDataSharingApproved()) ? "(Consent: Approved)" : "(Consent: Denied)"; // Added null check
                String parentName = (i != null && i.getParentName() != null) ? i.getParentName() : "Unknown Parent"; // Added null check
                setText(e||i==null?null:"Patient (Parent: "+ parentName +") " + consent);
            }
        });
    }

    // --- Data Loading ---
    private void loadMyBlogs() { if (!isCurrentUserValidForQuery()) return; blogs.clear(); if (!ThanalApp.isFirebaseInitialized()) return; try { Firestore db = FirestoreClient.getFirestore(); ApiFuture<QuerySnapshot> f = db.collection("blogs").whereEqualTo("authorEmail", currentUser.getEmail()).orderBy("createdAt", Query.Direction.DESCENDING).limit(50).get(); f.addListener(()->{ try { List<Blog> l=new ArrayList<>(); for(QueryDocumentSnapshot d:f.get().getDocuments()) l.add(d.toObject(Blog.class)); Platform.runLater(()->blogs.setAll(l)); } catch (Exception e){handleFirestoreError("load my blogs",e);} }, firestoreExecutor); } catch (Exception e){handleFirestoreError("init my blogs load",e);} }

    private void loadConsultationRequests() {
        if (!isCurrentUserValidForQuery()) return;
        consultations.clear();
        if (!ThanalApp.isFirebaseInitialized()) return;
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> f = db.collection("consultations")
                    .whereEqualTo("doctorEmail", currentUser.getEmail())
                    .whereEqualTo("status", "REQUESTED")
                    .orderBy("requestedAt", Query.Direction.ASCENDING).get();

            f.addListener(()->{
                try {
                    List<Consultation> l=new ArrayList<>();
                    for(QueryDocumentSnapshot d:f.get().getDocuments()) {
                        Consultation c=d.toObject(Consultation.class);
                        if(c!=null){
                            c.setConsultId(d.getId());
                            l.add(c);
                        }
                    }
                    Platform.runLater(()->consultations.setAll(l));
                } catch (Exception e){
                    handleFirestoreError("load consult reqs",e);
                }
            }, firestoreExecutor);
        } catch (Exception e){
            handleFirestoreError("init consult req load",e);
        }
    }

    private void loadUnansweredQuestions() { questions.clear(); if (!ThanalApp.isFirebaseInitialized()) return; try { Firestore db = FirestoreClient.getFirestore(); ApiFuture<QuerySnapshot> f = db.collection("questions").orderBy("timestamp", Query.Direction.DESCENDING).limit(50).get(); f.addListener(()->{ try { List<QnQuestion> l=new ArrayList<>(); for(QueryDocumentSnapshot d:f.get().getDocuments()){ QnQuestion q = d.toObject(QnQuestion.class); if(q!=null) { q.setDocumentId(d.getId()); l.add(q); } } Platform.runLater(()->questions.setAll(l)); } catch (Exception e){handleFirestoreError("load questions",e);} }, firestoreExecutor); } catch (Exception e){handleFirestoreError("init question load",e);} }

    // --- Includes Debug Statements ---
    private void loadApprovedPatients() {
        if (!isCurrentUserValidForQuery()) {
            System.out.println("DEBUG: loadApprovedPatients skipped - currentUser invalid or email missing.");
            return;
        }
        approvedPatients.clear();
        if (!ThanalApp.isFirebaseInitialized()) {
            System.out.println("DEBUG: loadApprovedPatients skipped - Firebase not initialized.");
            return;
        }
        System.out.println("DEBUG: Starting loadApprovedPatients for doctor: " + currentUser.getEmail()); // Log which doctor
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> f = db.collection("consultations")
                    .whereEqualTo("doctorEmail", currentUser.getEmail())
                    .whereEqualTo("status", "ACCEPTED")
                    .orderBy("requestedAt", Query.Direction.DESCENDING)
                    .get();

            f.addListener(() -> {
                try {
                    QuerySnapshot querySnapshot = f.get(); // Get the results
                    System.out.println("DEBUG: Found " + querySnapshot.size() + " accepted consultations in Firestore."); // Log how many matched

                    List<Consultation> l = new ArrayList<>();
                    for (QueryDocumentSnapshot d : querySnapshot.getDocuments()) {
                        System.out.println("DEBUG: Processing document ID: " + d.getId()); // Log each doc ID
                        Consultation c = null;
                        try {
                            c = d.toObject(Consultation.class); // Attempt conversion
                            System.out.println("DEBUG: Successfully mapped document " + d.getId() + " to Consultation object.");
                        } catch (Exception mappingError) {
                            // Log the specific error if toObject fails
                            System.err.println("DEBUG: FAILED to map document " + d.getId() + " to Consultation object.");
                            mappingError.printStackTrace(); // Print the full stack trace for this error
                        }

                        if (c != null) {
                            c.setConsultId(d.getId());
                            l.add(c);
                        } else {
                            System.out.println("DEBUG: Consultation object was null after mapping attempt for doc " + d.getId());
                        }
                    }
                    // This part runs on the JavaFX thread
                    Platform.runLater(() -> {
                        System.out.println("DEBUG: Setting " + l.size() + " approved patients in the UI list."); // Log how many are being added to UI
                        approvedPatients.setAll(l);
                        if (l.isEmpty() && querySnapshot.size() > 0) {
                            System.err.println("DEBUG: WARNING - Firestore found documents, but the list added to UI is empty. Check mapping errors above.");
                        } else if (l.isEmpty() && querySnapshot.isEmpty()){
                            System.out.println("DEBUG: Firestore query returned no matching documents, UI list is correctly empty.");
                        }
                    });
                } catch (Exception e) {
                    // Log specific error during data processing/mapping
                    System.err.println("DEBUG: Specific error during approvedPatients data processing: " + e.getClass().getName() + " - " + e.getMessage());
                    handleFirestoreError("load approved patients", e);
                }
            }, firestoreExecutor);
        } catch (Exception e) {
            handleFirestoreError("init approved patients load", e);
        }
    }


    // --- Action Handlers ---
    @FXML void handleLogout(ActionEvent event) throws IOException { SessionManager.getInstance().clearSession(); SceneSwitcher.switchScene(event, "home-page.fxml"); }
    @FXML void acceptConsultation() { Consultation sel = consultationRequestsListView.getSelectionModel().getSelectedItem(); if(sel==null) {showAlert(Alert.AlertType.WARNING,"Select","Select request."); return;} updateConsultationStatus(sel, "ACCEPTED", "Consultation accepted."); }
    @FXML void declineConsultation() { Consultation sel = consultationRequestsListView.getSelectionModel().getSelectedItem(); if(sel==null) {showAlert(Alert.AlertType.WARNING,"Select","Select request."); return;} updateConsultationStatus(sel, "DECLINED", "Consultation declined."); }

    private void updateConsultationStatus(Consultation cons, String status, String msg) {
        if (!ThanalApp.isFirebaseInitialized()) { handleFirestoreError("update consult",null); return; }
        if(isNullOrEmpty(cons.getConsultId())) {
            showAlert(Alert.AlertType.ERROR,"Data Error","Invalid consultation ID.");
            return;
        }
        try {
            Firestore db=FirestoreClient.getFirestore();
            DocumentReference ref=db.collection("consultations").document(cons.getConsultId());
            ApiFuture<WriteResult> f=ref.update("status",status,"lastUpdatedAt",Timestamp.now());
            f.addListener(()->{
                try {
                    f.get();
                    Platform.runLater(()->{
                        consultations.remove(cons);
                        showAlert(Alert.AlertType.INFORMATION,"Success",msg);
                        if (status.equals("ACCEPTED")) {
                            loadApprovedPatients(); // Refresh the approved list
                        }
                    });
                } catch (Exception e){
                    handleFirestoreError("update consult status",e);
                }
            }, firestoreExecutor);
        } catch (Exception e){
            handleFirestoreError("setup consult update",e);
        }
    }

    @FXML void submitAnswer() { QnQuestion selQ = unansweredQuestionsListView.getSelectionModel().getSelectedItem(); String ans = answerArea.getText(); if(selQ==null) {showAlert(Alert.AlertType.WARNING,"Select","Select question."); return;} if(isNullOrEmpty(ans)) {showAlert(Alert.AlertType.WARNING,"Input Error","Answer empty."); return;} if(!isCurrentUserValid()) return; if(isNullOrEmpty(selQ.getDocumentId())) {showAlert(Alert.AlertType.ERROR,"Data Error","Invalid Question ID."); return;} Map<String,Object> data=new HashMap<>(); data.put("questionId",selQ.getDocumentId()); data.put("questionTitle",selQ.getTitle()); data.put("authorId",currentUser.getUserId()!=null?currentUser.getUserId():-1L); data.put("authorEmail",currentUser.getEmail()); data.put("authorName",currentUser.getName()); data.put("content",ans); data.put("answeredAt",Timestamp.now()); saveToFirestore("answers",data,"Answer submitted.",()->{ answerArea.clear(); loadUnansweredQuestions(); }); }

    @FXML
    void viewPatientReport() {
        Consultation selP = approvedPatientsListView.getSelectionModel().getSelectedItem();
        if(selP == null) {
            showAlert(Alert.AlertType.WARNING,"Select","Select patient.");
            return;
        }

        // Enforce Consent Check
        if (!selP.isDataSharingApproved()) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "The parent has not approved data sharing for this consultation.");
            return;
        }

        String parentEmail = selP.getParentEmail();
        if (isNullOrEmpty(parentEmail)) {
            showAlert(Alert.AlertType.ERROR, "Data Error", "Cannot find parent's email for this patient.");
            return;
        }

        try {
            // Recreate the folder name exactly as RegistrationController does
            String userSubDir = parentEmail.replaceAll("[^a-zA-Z0-9.-]", "_");
            Path userDirPath = Paths.get(LOCAL_UPLOAD_DIR, userSubDir);

            if (Files.exists(userDirPath) && Files.isDirectory(userDirPath)) {
                File directory = userDirPath.toFile();
                // Open the directory in the system's file explorer
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(directory);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not open file explorer on this system.");
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No Reports", "No reports folder found for this parent.\nPath: " + userDirPath);
                System.err.println("Could not find directory: " + userDirPath);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open reports folder: " + e.getMessage());
        }
    }

    @FXML void writeNewBlog(ActionEvent event) { try { SceneSwitcher.switchScene(event, "write-blog.fxml"); } catch (Exception e) { showAlert(Alert.AlertType.ERROR,"Nav Error","Cannot open blog editor."); e.printStackTrace(); } }

    // --- Helpers ---
    private void saveToFirestore(String coll, Map<String,Object> data, String msg, Runnable action) { if (!ThanalApp.isFirebaseInitialized()) { handleFirestoreError("save "+coll, null); return; } try { Firestore db=FirestoreClient.getFirestore(); ApiFuture<WriteResult> f=db.collection(coll).document().set(data); f.addListener(()->{ try { f.get(); Platform.runLater(()->{ if(action!=null)action.run(); if(!isNullOrEmpty(msg))showAlert(Alert.AlertType.INFORMATION,"Success",msg); }); } catch(Exception e){handleFirestoreError("save "+coll+" result", e);} }, firestoreExecutor); } catch (Exception e){handleFirestoreError("submit "+coll, e);} }
    private boolean isCurrentUserValid() { if (currentUser == null) { showAlert(Alert.AlertType.ERROR, "Action Error", "Session invalid."); return false; } return true; }
    private boolean isCurrentUserValidForQuery() { if (currentUser == null || isNullOrEmpty(currentUser.getEmail())) { System.err.println("Cannot query: doctor/email missing."); return false; } return true; }
    private void showAlert(Alert.AlertType t, String title, String msg) { Platform.runLater(()->new Alert(t, msg){{setTitle(title);setHeaderText(null);}}.showAndWait()); }
    private void handleFirestoreError(String op, Exception e) { String msg=(e!=null)?e.getMessage():"Unknown"; System.err.println("Firestore error ["+op+"]: "+msg); if(e!=null)e.printStackTrace(); Platform.runLater(()->showAlert(Alert.AlertType.ERROR, "DB Error", "Op ["+op+"] failed.")); }
    private boolean isNullOrEmpty(String s) { return s == null || s.trim().isEmpty(); }
    private void disableUIComponents(){ welcomeLabel.setText("Doctor Dashboard - OFFLINE"); /* Disable list views, text areas, buttons */ }

} // End of DoctorDashboardController class