package com.example.thanal.controller;

import com.example.thanal.main.ThanalApp; // Import ThanalApp
import com.example.thanal.util.SceneSwitcher;
import com.example.thanal.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox; // Import VBox

// --- STANDARD JAVA IMPORTS ---
import java.io.IOException; // For handleLogout
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional; // For confirmation dialog
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService; // Import ExecutorService
import java.text.SimpleDateFormat; // For formatting date
import java.util.HashMap; // <-- ***** ADDED THIS IMPORT *****

// --- ADDED IMPORTS FOR FILE HANDLING ---
import java.awt.Desktop; // To open files/folders
import java.io.File;    // To represent files
import java.nio.file.InvalidPathException;
import java.nio.file.Paths; // To create File objects from paths

// --- FIREBASE IMPORTS ---
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
// import com.google.firebase.FirebaseApp; // Not needed directly here
import com.google.firebase.cloud.FirestoreClient;
// ---

public class AdminDashboardController {

    @FXML private ListView<Map<String, Object>> userRequestsListView; // Store data map from Firestore
    @FXML private ListView<Map<String, Object>> allUsersListView; // Store data map from Firestore

    // --- ADDED FXML Fields for Details Pane ---
    @FXML private VBox pendingUserDetailsBox;
    @FXML private Label detailNameLabel;
    @FXML private Label detailEmailLabel;
    @FXML private Label detailRoleLabel;
    @FXML private Label detailPhoneLabel;
    @FXML private Label detailRegisteredAtLabel;
    @FXML private Label detailAadhaarLabel;
    @FXML private Label detailLicenseLabel;
    @FXML private Label detailMedicalLabel;
    @FXML private Label detailDisabilityLabel;
    // ---

    // Observable lists bound to UI
    private final ObservableList<Map<String, Object>> pendingUsers = FXCollections.observableArrayList();
    private final ObservableList<Map<String, Object>> approvedUsers = FXCollections.observableArrayList();
    // Executor for background Firestore tasks
    private final ExecutorService firestoreExecutor = Executors.newSingleThreadExecutor();

    // Store the currently selected pending user's data
    private Map<String, Object> selectedPendingUserData = null;

    @FXML
    public void initialize() {
        // Ensure Firebase is initialized
        if (!ThanalApp.isFirebaseInitialized()) {
            showAlert(AlertType.ERROR, "Initialization Error", "Firebase connection not ready. Admin functions disabled.");
            disableUIComponents();
            return;
        }

        // Bind lists
        userRequestsListView.setItems(pendingUsers);
        allUsersListView.setItems(approvedUsers);

        // Configure cell factories
        configureListViewFactory(userRequestsListView);
        configureListViewFactory(allUsersListView);

        // --- ADDED: Listener to update details pane ---
        userRequestsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedPendingUserData = newSelection; // Store selected data
            updateDetailsPane(newSelection);
        });
        // ---

        // Initial state for details box
        pendingUserDetailsBox.setVisible(false);

        // Load data
        loadPendingUsers();
        loadApprovedUsers();
    }

    // Helper to configure cell factories
    private void configureListViewFactory(ListView<Map<String, Object>> listView) {
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    String email = (String) item.getOrDefault("email", "N/A");
                    String role = (String) item.getOrDefault("role", "N/A");
                    String name = (String) item.getOrDefault("name", "No Name");
                    setText(name + " (" + email + ") - Role: " + role);
                }
            }
        });
    }

    // --- ADDED: Method to update the details pane ---
    private void updateDetailsPane(Map<String, Object> userData) {
        if (userData != null) {
            detailNameLabel.setText(getStringOrDefault(userData, "name", "-"));
            detailEmailLabel.setText(getStringOrDefault(userData, "email", "-"));
            detailRoleLabel.setText(getStringOrDefault(userData, "role", "-"));
            detailPhoneLabel.setText(getStringOrDefault(userData, "phone", "-"));

            Timestamp ts = (Timestamp) userData.get("registeredAt");
            String formattedDate = "-";
            if (ts != null) {
                formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(ts.toDate());
            }
            detailRegisteredAtLabel.setText(formattedDate);

            detailAadhaarLabel.setText(getStringOrDefault(userData, "aadhaarFilePath", "Not Provided"));
            detailLicenseLabel.setText(getStringOrDefault(userData, "licenseFilePath", "Not Provided / N/A"));
            detailMedicalLabel.setText(getStringOrDefault(userData, "medicalReportPath", "Not Provided / N/A"));
            detailDisabilityLabel.setText(getStringOrDefault(userData, "disabilityReportPath", "Not Provided / N/A"));

            pendingUserDetailsBox.setVisible(true); // Show the details box
        } else {
            // Clear details if no user is selected
            detailNameLabel.setText("-");
            detailEmailLabel.setText("-");
            detailRoleLabel.setText("-");
            detailPhoneLabel.setText("-");
            detailRegisteredAtLabel.setText("-");
            detailAadhaarLabel.setText("-");
            detailLicenseLabel.setText("-");
            detailMedicalLabel.setText("-");
            detailDisabilityLabel.setText("-");
            pendingUserDetailsBox.setVisible(false); // Hide the details box
        }
    }
    // Helper to safely get string values from the map
    private String getStringOrDefault(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return (value instanceof String && !isNullOrEmpty((String)value)) ? (String) value : defaultValue;
    }
    // ---

    private void loadPendingUsers() {
        pendingUsers.clear();
        if (!ThanalApp.isFirebaseInitialized()) return;
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection("pending_users")
                    .whereEqualTo("status", "pending")
                    .orderBy("registeredAt", Query.Direction.ASCENDING)
                    .limit(100).get();

            future.addListener(() -> {
                try {
                    List<Map<String, Object>> loaded = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
                        Map<String, Object> data = doc.getData();
                        data.put("id", doc.getId()); // Store email as ID
                        loaded.add(data);
                    }
                    Platform.runLater(() -> {
                        pendingUsers.setAll(loaded);
                        updateDetailsPane(null); // Clear details after reload
                    });
                } catch (Exception e) { handleFirestoreError("load pending users", e); }
            }, firestoreExecutor);
        } catch (Exception e) { handleFirestoreError("init pending users load", e); }
    }

    private void loadApprovedUsers() {
        approvedUsers.clear();
        if (!ThanalApp.isFirebaseInitialized()) return;
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection("users").limit(200).get();

            future.addListener(() -> {
                try {
                    List<Map<String, Object>> loaded = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
                        Map<String, Object> data = doc.getData();
                        data.put("id", doc.getId()); // Store email as ID
                        loaded.add(data);
                    }
                    Platform.runLater(() -> approvedUsers.setAll(loaded));
                } catch (Exception e) { handleFirestoreError("load approved users", e); }
            }, firestoreExecutor);
        } catch (Exception e) { handleFirestoreError("init approved users load", e); }
    }

    @FXML
    void approveUser() {
        // Use the stored selected data instead of getting from list view directly
        Map<String, Object> selectedUserMap = selectedPendingUserData; // userRequestsListView.getSelectionModel().getSelectedItem();

        if (selectedUserMap == null) { showAlert(AlertType.WARNING, "Selection Error", "Select user request from the list."); return; }
        String userEmail = (String) selectedUserMap.get("id");
        if (isNullOrEmpty(userEmail)) { showAlert(AlertType.ERROR, "Data Error", "User email missing in selected data."); return; }

        Alert confirm = new Alert(AlertType.CONFIRMATION, "Approve " + userEmail + "?", ButtonType.YES, ButtonType.CANCEL);
        confirm.setTitle("Confirm Approval"); confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (!ThanalApp.isFirebaseInitialized()) { handleFirestoreError("approve user (Firebase not init)", null); return; }
            try {
                Firestore db = FirestoreClient.getFirestore();
                DocumentReference pendingRef = db.collection("pending_users").document(userEmail);
                DocumentReference approvedRef = db.collection("users").document(userEmail);

                ApiFuture<Void> transaction = db.runTransaction(tx -> {
                    DocumentSnapshot pendingSnap = tx.get(pendingRef).get();
                    if (!pendingSnap.exists()) {
                        Platform.runLater(()-> { showAlert(AlertType.INFORMATION, "Already Processed", userEmail + " no longer pending."); loadPendingUsers(); });
                        throw new RuntimeException(userEmail + " no longer pending.");
                    }
                    Map<String, Object> data = pendingSnap.getData();
                    if (data == null) throw new RuntimeException("Pending data null for " + userEmail);

                    // Prepare data for the 'users' collection
                    Map<String, Object> approvedData = new HashMap<>(data); // Copy existing data
                    approvedData.remove("status"); // Remove pending status
                    approvedData.remove("registeredAt"); // Remove registration time
                    approvedData.put("approvedAt", Timestamp.now()); // Add approval time

                    // !! IMPORTANT SECURITY NOTE: Remove plain text password before moving !!
                    // For a real app using Firebase Auth, this password field wouldn't exist here.
                    // Since it does in this demo, it's better to remove it than copy it.
                    approvedData.remove("password");

                    tx.set(approvedRef, approvedData); // Create user in 'users' collection
                    tx.delete(pendingRef);             // Delete from 'pending_users'
                    return null;
                });

                transaction.addListener(() -> {
                    try {
                        transaction.get(); // Check transaction errors
                        Platform.runLater(() -> {
                            showAlert(AlertType.INFORMATION, "Success", userEmail + " approved.");
                            loadPendingUsers(); // Refresh pending list
                            loadApprovedUsers(); // Refresh approved list
                            selectedPendingUserData = null; // Clear selection data
                        });
                    } catch (Exception e) {
                        if (e.getCause() instanceof RuntimeException && e.getCause().getMessage().contains("no longer pending")) {
                            System.out.println("Approval transaction aborted gracefully: " + e.getCause().getMessage());
                            // Alert was already shown in the transaction lambda
                        } else {
                            handleFirestoreError("approve transaction", e);
                        }
                    }
                }, firestoreExecutor);
            } catch (Exception e) { handleFirestoreError("approve setup", e); }
        } else { System.out.println("Approval cancelled."); }
    }


    @FXML
    void deleteUser() {
        // Determine which list view has the current selection
        ListView<Map<String, Object>> sourceLV;
        boolean isPendingListSelected = !userRequestsListView.getSelectionModel().isEmpty();
        boolean isApprovedListSelected = !allUsersListView.getSelectionModel().isEmpty();

        Map<String, Object> userMap = null;
        if (isPendingListSelected) {
            sourceLV = userRequestsListView;
            userMap = selectedPendingUserData; // Use stored data
        } else if (isApprovedListSelected) {
            sourceLV = allUsersListView;
            userMap = sourceLV.getSelectionModel().getSelectedItem(); // Get directly
        } else {
            showAlert(AlertType.WARNING, "Selection Error", "Select a user from either list to delete/reject."); return;
        }

        if (userMap == null) { showAlert(AlertType.WARNING, "Selection Error", "Select user to delete/reject."); return; }

        String email = (String) userMap.get("id");
        boolean isPending = "pending".equals(userMap.get("status")); // Check status field if present
        String collection = isPending ? "pending_users" : "users";
        String actionVerb = isPending ? "Reject" : "Delete";

        if (isNullOrEmpty(email)) { showAlert(AlertType.ERROR, "Data Error", "User email missing."); return; }
        if (!isPending && "admin@thanal.com".equalsIgnoreCase(email)) { showAlert(AlertType.WARNING, "Denied", "Cannot delete primary admin account."); return; }

        Alert confirm = new Alert(AlertType.CONFIRMATION, actionVerb + " " + email + "? This cannot be undone.", ButtonType.YES, ButtonType.CANCEL);
        confirm.setTitle("Confirm " + actionVerb); confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (!ThanalApp.isFirebaseInitialized()) { handleFirestoreError("delete user (Firebase not init)", null); return; }
            try {
                Firestore db = FirestoreClient.getFirestore();
                ApiFuture<WriteResult> deleteFuture = db.collection(collection).document(email).delete();

                // Consider adding logic here to delete files from Cloud Storage if implemented
                // For local files, attempting deletion might be complex due to permissions.
                // It might be safer to just leave the local files orphaned.

                deleteFuture.addListener(() -> {
                    try {
                        deleteFuture.get(); // Check delete errors
                        Platform.runLater(() -> {
                            showAlert(AlertType.INFORMATION, "Success", email + (isPending ? " rejected." : " deleted."));
                            if (isPending) {
                                loadPendingUsers(); // Refresh pending
                                selectedPendingUserData = null; // Clear selection data
                            } else {
                                loadApprovedUsers(); // Refresh approved
                            }
                        });
                    } catch (Exception e) { handleFirestoreError("delete completion", e); }
                }, firestoreExecutor);
            } catch (Exception e) { handleFirestoreError("delete setup", e); }
        } else { System.out.println(actionVerb + " cancelled."); }
    }

    // --- ADDED: Action Handlers for Opening Files ---

    @FXML void openAadhaarFile(ActionEvent event) { openFileFromPathKey("aadhaarFilePath"); }
    @FXML void openLicenseFile(ActionEvent event) { openFileFromPathKey("licenseFilePath"); }
    @FXML void openMedicalFile(ActionEvent event) { openFileFromPathKey("medicalReportPath"); }
    @FXML void openDisabilityFile(ActionEvent event) { openFileFromPathKey("disabilityReportPath"); }

    private void openFileFromPathKey(String pathKey) {
        if (selectedPendingUserData == null) {
            showAlert(AlertType.WARNING, "Selection Error", "Select a pending user first."); return;
        }
        String filePath = getStringOrDefault(selectedPendingUserData, pathKey, null);
        if (isNullOrEmpty(filePath) || filePath.equals("Not Provided / N/A") || filePath.equals("-")) { // Added check for "-"
            showAlert(AlertType.INFORMATION, "File Not Found", "No file path was provided for this item."); return;
        }
        openFile(filePath);
    }

    @FXML void openUserFolder(ActionEvent event) {
        if (selectedPendingUserData == null) {
            showAlert(AlertType.WARNING, "Selection Error", "Select a pending user first."); return;
        }
        // Try to get a valid path from any of the fields
        String filePath = getStringOrDefault(selectedPendingUserData, "aadhaarFilePath", null);
        if (isNullOrEmpty(filePath) || filePath.equals("-")) filePath = getStringOrDefault(selectedPendingUserData, "licenseFilePath", null);
        if (isNullOrEmpty(filePath) || filePath.equals("-")) filePath = getStringOrDefault(selectedPendingUserData, "medicalReportPath", null);
        if (isNullOrEmpty(filePath) || filePath.equals("-")) filePath = getStringOrDefault(selectedPendingUserData, "disabilityReportPath", null);


        if (isNullOrEmpty(filePath) || filePath.equals("-")) {
            showAlert(AlertType.INFORMATION, "Folder Not Found", "Cannot determine user folder (no valid file paths found)."); return;
        }
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile(); // Get the containing directory
            if (parentDir != null && parentDir.exists() && parentDir.isDirectory()) {
                openDirectory(parentDir);
            } else {
                showAlert(AlertType.ERROR, "Folder Error", "Could not find or access the user's folder:\n" + (parentDir != null ? parentDir.getAbsolutePath() : "Invalid base path"));
            }
        } catch (InvalidPathException | NullPointerException e) {
            showAlert(AlertType.ERROR, "Path Error", "Invalid file path stored: " + filePath);
        }
    }


    // --- ADDED: Helper to open file or directory ---
    private void openFile(String filePathString) {
        try {
            File file = Paths.get(filePathString).toFile();
            if (file.exists() && file.isFile()) { // Check if it's actually a file
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(file);
                } else {
                    showAlert(AlertType.ERROR, "Unsupported Action", "Cannot open files on this system.");
                }
            } else {
                showAlert(AlertType.WARNING, "File Not Found", "The file could not be found or is not a file:\n" + filePathString);
            }
        } catch (InvalidPathException e) {
            showAlert(AlertType.ERROR, "Invalid Path", "The stored file path is invalid:\n" + filePathString);
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error Opening File", "Could not open the file:\n" + e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void openDirectory(File directory) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(directory);
            } else {
                showAlert(AlertType.ERROR, "Unsupported Action", "Cannot open folders on this system.");
            }
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error Opening Folder", "Could not open the folder:\n" + e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    // ---


    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        SessionManager.getInstance().clearSession();
        SceneSwitcher.switchScene(event, "home-page.fxml");
    }

    // --- Helper Methods ---
    private void showAlert(AlertType type, String title, String msg) { Platform.runLater(() -> new Alert(type, msg){{setTitle(title); setHeaderText(null);}}.showAndWait()); }
    private void handleFirestoreError(String op, Exception e) { String msg=(e!=null)?e.getMessage():"Unknown"; System.err.println("Firestore error ["+op+"]: "+msg); if(e!=null) e.printStackTrace(); Platform.runLater(() -> showAlert(AlertType.ERROR, "Database Error", "Op ["+op+"] failed. Check logs.")); }
    private boolean isNullOrEmpty(String s) { return s == null || s.trim().isEmpty(); }
    private void disableUIComponents(){ userRequestsListView.setDisable(true); allUsersListView.setDisable(true); pendingUserDetailsBox.setVisible(false); /* Disable buttons */ System.out.println("Admin UI Disabled"); }

} // End of AdminDashboardController class