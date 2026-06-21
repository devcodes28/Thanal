package com.example.thanal.controller;

// --- JavaFX Imports ---
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// --- Standard Java Imports ---
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

// --- Firebase Imports ---
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

// --- Project Util Imports ---
import com.example.thanal.main.ThanalApp;
import com.example.thanal.util.SceneSwitcher;
import com.example.thanal.util.SessionManager;

// --- Project Model Imports ---
import com.example.thanal.model.*;

// --- Controller Class ---
public class ParentDashboardController {

    // --- FXML Fields ---
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
    @FXML private TextArea answerArea;
    @FXML private ListView<QnAnswer> answerListView;
    @FXML private ListView<Comment> commentListView;

    // --- ADDED FXML FIELD FOR CHECKBOX ---
    @FXML private CheckBox dataSharingCheckbox;

    // --- Class Variables ---
    private Parent currentUser;
    private final ObservableList<Blog> blogs = FXCollections.observableArrayList();
    private final ObservableList<QnQuestion> questions = FXCollections.observableArrayList();
    private final ObservableList<Doctor> availableDoctors = FXCollections.observableArrayList();
    private final ObservableList<BehaviorLog> behaviorLogs = FXCollections.observableArrayList();
    private final ObservableList<FinancialApplication> financialApplications = FXCollections.observableArrayList();
    private final ObservableList<QnAnswer> answers = FXCollections.observableArrayList();
    private final ObservableList<Comment> comments = FXCollections.observableArrayList();
    private final ExecutorService firestoreExecutor = Executors.newSingleThreadExecutor();

    // --- Initialization ---
    @FXML
    public void initialize() {
        if (!ThanalApp.isFirebaseInitialized()) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Firebase connection not ready.");
            disableUIComponents(); return;
        }
        User user = SessionManager.getInstance().getCurrentUser();
        if (user instanceof Parent) {
            this.currentUser = (Parent) user;
            if (isNullOrEmpty(this.currentUser.getEmail())) {
                showAlert(Alert.AlertType.ERROR, "Login Error", "Parent email missing.");
                disableUIComponents(); return;
            }
            welcomeLabel.setText("Welcome, " + currentUser.getName());
        } else {
            welcomeLabel.setText("Parent Dashboard - Error");
            showAlert(Alert.AlertType.ERROR, "Login Error", "Could not identify parent.");
            disableUIComponents(); return;
        }
        bindLists();
        setupUIComponents();
        loadAllData();
    }

    // --- UI Setup ---
    private void bindLists(){
        blogListView.setItems(blogs); qnaListView.setItems(questions);
        doctorListView.setItems(availableDoctors); behaviorLogListView.setItems(behaviorLogs);
        financialAidListView.setItems(financialApplications);
        if (answerListView != null) answerListView.setItems(answers);
        if (commentListView != null) commentListView.setItems(comments);
    }
    private void setupUIComponents(){
        setupBlogListView(); setupQnaListView(); setupDoctorListView();
        setupBehaviorTracking(); setupFinancialAid();
        setupAnswerListView();
        setupCommentListView();
    }
    private void loadAllData(){
        loadBlogsFromFirestore(); loadQuestionsFromFirestore(); loadDoctorsFromFirestore();
        loadBehaviorLogsFromFirestore(); loadFinancialAidApplications();
    }

    private void setupBlogListView() {
        blogListView.setCellFactory(param -> new ListCell<>() {
            @Override protected void updateItem(Blog item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.getTitle()); }
        });
        blogListView.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            blogContentView.setText(val != null ? val.getContent() : "");
            commentField.setPromptText(val != null ? "Comment on: " + val.getTitle() : "Write a comment...");
            commentField.clear();
            if (val != null) {
                loadCommentsForBlog(val);
            } else {
                comments.clear();
            }
        });
    }

    private void setupCommentListView() {
        if (commentListView == null) return;
        commentListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Comment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String author = item.getAuthorName() != null ? item.getAuthorName() : "Unknown";
                    setText(item.getContent() + "\n- " + author);
                    setWrapText(true);
                }
            }
        });
    }

    private void setupQnaListView() {
        qnaListView.setCellFactory(param -> new ListCell<>() {
            @Override protected void updateItem(QnQuestion item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.getTitle()); }
        });
        qnaListView.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (answerArea != null) {
                answerArea.setPromptText(nv != null ? "Answer: \"" + nv.getTitle() + "\"" : "Select a question above to answer...");
            }
            if (nv != null) {
                loadAnswersForQuestion(nv);
            } else {
                answers.clear();
            }
        });
    }

    private void setupAnswerListView() {
        if (answerListView == null) return;
        answerListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(QnAnswer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String author = item.getAuthorName() != null ? item.getAuthorName() : "Unknown";
                    setText(item.getContent() + "\n- Answered by " + author);
                    setWrapText(true);
                }
            }
        });
    }

    private void setupDoctorListView() {
        doctorListView.setCellFactory(param -> new ListCell<>() {
            @Override protected void updateItem(Doctor item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.getName() + " - " + item.getSpecialization()); }
        });
    }
    private void setupBehaviorTracking() {
        behaviorLogListView.setCellFactory(param -> new ListCell<>() {
            @Override protected void updateItem(BehaviorLog item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getDate() == null) setText(null);
                else setText(item.getDate() + ": " + item.getBehaviors());
            }
        });
    }
    private void setupFinancialAid() {
        govtDeptComboBox.getItems().addAll("National Trust Scheme", "State Disability Fund", "Health Ministry Grant", "Other");
        financialAidListView.setCellFactory(param -> new ListCell<>() {
            @Override protected void updateItem(FinancialApplication item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : "To: " + item.getGovtDepartment() + " | Status: " + item.getStatus()); }
        });
    }

    // --- Data Loading ---
    private void loadBlogsFromFirestore() {
        blogs.clear(); if (!ThanalApp.isFirebaseInitialized()) return;
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection("blogs").orderBy("createdAt", Query.Direction.DESCENDING).limit(50).get();
            future.addListener(() -> {
                try {
                    List<Blog> loaded = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
                        Blog blog = doc.toObject(Blog.class);
                        if (blog != null) {
                            blog.setDocumentId(doc.getId());
                            loaded.add(blog);
                        }
                    }
                    Platform.runLater(() -> blogs.setAll(loaded));
                } catch (Exception e) {
                    handleFirestoreError("load blogs", e);
                }
            }, firestoreExecutor);
        } catch (Exception e) {
            handleFirestoreError("init blog load", e);
        }
    }

    private void loadQuestionsFromFirestore() {
        questions.clear(); if (!ThanalApp.isFirebaseInitialized()) return;
        try { Firestore db = FirestoreClient.getFirestore(); ApiFuture<QuerySnapshot> future = db.collection("questions").orderBy("timestamp", Query.Direction.DESCENDING).limit(100).get();
            future.addListener(() -> { try { List<QnQuestion> loaded = new ArrayList<>(); for (QueryDocumentSnapshot doc : future.get().getDocuments()) { QnQuestion q = doc.toObject(QnQuestion.class); if(q!=null) { q.setDocumentId(doc.getId()); loaded.add(q); } } Platform.runLater(() -> questions.setAll(loaded)); } catch (Exception e) { handleFirestoreError("load questions", e); } }, firestoreExecutor);
        } catch (Exception e) { handleFirestoreError("init question load", e); }
    }

    private void loadAnswersForQuestion(QnQuestion question) {
        if (question == null || isNullOrEmpty(question.getDocumentId())) {
            answers.clear(); return;
        }
        answers.clear();
        if (!ThanalApp.isFirebaseInitialized()) return;

        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection("answers")
                    .whereEqualTo("questionId", question.getDocumentId())
                    .orderBy("answeredAt", Query.Direction.ASCENDING)
                    .get();

            future.addListener(() -> {
                try {
                    List<QnAnswer> loaded = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
                        QnAnswer ans = doc.toObject(QnAnswer.class);
                        if(ans != null) loaded.add(ans);
                    }
                    Platform.runLater(() -> answers.setAll(loaded));
                } catch (Exception e) {
                    handleFirestoreError("load answers", e);
                }
            }, firestoreExecutor);
        } catch (Exception e) {
            handleFirestoreError("init answer load", e);
        }
    }

    private void loadCommentsForBlog(Blog blog) {
        if (blog == null || isNullOrEmpty(blog.getDocumentId())) {
            comments.clear(); return;
        }
        comments.clear();
        if (!ThanalApp.isFirebaseInitialized()) return;

        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection("comments")
                    .whereEqualTo("blogId", blog.getDocumentId())
                    .orderBy("commentedAt", Query.Direction.ASCENDING)
                    .get();

            future.addListener(() -> {
                try {
                    List<Comment> loaded = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
                        Comment c = doc.toObject(Comment.class);
                        if(c != null) loaded.add(c);
                    }
                    Platform.runLater(() -> comments.setAll(loaded));
                } catch (Exception e) {
                    handleFirestoreError("load comments", e);
                }
            }, firestoreExecutor);
        } catch (Exception e) {
            handleFirestoreError("init comment load", e);
        }
    }

    private void loadDoctorsFromFirestore() {
        availableDoctors.clear(); if (!ThanalApp.isFirebaseInitialized()) return;
        try { Firestore db = FirestoreClient.getFirestore(); ApiFuture<QuerySnapshot> future = db.collection("users").whereEqualTo("role", "doctor").get();
            future.addListener(() -> { try { List<Doctor> loaded = new ArrayList<>(); for (QueryDocumentSnapshot doc : future.get().getDocuments()) { Doctor d = doc.toObject(Doctor.class); if(d!=null) { d.setEmail(doc.getId()); d.setRole("doctor"); loaded.add(d); } } Platform.runLater(() -> availableDoctors.setAll(loaded)); } catch (Exception e) { handleFirestoreError("load doctors", e); } }, firestoreExecutor);
        } catch (Exception e) { handleFirestoreError("init doctor load", e); }
    }

    private void loadBehaviorLogsFromFirestore() {
        if (!isCurrentUserValidForQuery()) return; behaviorLogs.clear(); if (!ThanalApp.isFirebaseInitialized()) return;
        try { Firestore db = FirestoreClient.getFirestore(); ApiFuture<QuerySnapshot> future = db.collection("behavior_logs").whereEqualTo("parentEmail", currentUser.getEmail()).orderBy("date", Query.Direction.DESCENDING).limit(200).get();
            future.addListener(() -> { try { List<BehaviorLog> loaded = new ArrayList<>(); for (QueryDocumentSnapshot doc : future.get().getDocuments()) { try { BehaviorLog log = doc.toObject(BehaviorLog.class); String dateStr = doc.getString("date"); if(log!=null && dateStr!=null) { log.setDate(dateStr); loaded.add(log); } } catch(Exception dex){ System.err.println("Error parsing log doc: "+dex.getMessage());} } Platform.runLater(() -> behaviorLogs.setAll(loaded)); } catch (Exception e) { handleFirestoreError("load behavior logs", e); } }, firestoreExecutor);
        } catch (Exception e) { handleFirestoreError("init behavior log load", e); }
    }

    private void loadFinancialAidApplications() {
        if (!isCurrentUserValidForQuery()) return; financialApplications.clear(); if (!ThanalApp.isFirebaseInitialized()) return;
        try { Firestore db = FirestoreClient.getFirestore(); ApiFuture<QuerySnapshot> future = db.collection("financial_applications").whereEqualTo("parentEmail", currentUser.getEmail()).orderBy("submittedAt", Query.Direction.DESCENDING).limit(50).get();
            future.addListener(() -> { try { List<FinancialApplication> loaded = new ArrayList<>(); for (QueryDocumentSnapshot doc : future.get().getDocuments()) { FinancialApplication app = doc.toObject(FinancialApplication.class); if(app!=null) { loaded.add(app); } } Platform.runLater(() -> financialApplications.setAll(loaded)); } catch (Exception e) { handleFirestoreError("load financial apps", e); } }, firestoreExecutor);
        } catch (Exception e) { handleFirestoreError("init financial app load", e); }
    }

    // --- Action Handlers ---
    @FXML void submitBehaviorLog() { String b = behaviourField.getText(); if (isNullOrEmpty(b)) { showAlert(Alert.AlertType.WARNING,"Input Error","Behavior empty."); return; } if (!isCurrentUserValid()) return; String nowDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE); Map<String,Object> d = new HashMap<>(); d.put("parentId",currentUser.getUserId()!=null?currentUser.getUserId():-1L); d.put("parentEmail",currentUser.getEmail()); d.put("date",nowDate); d.put("behaviors",b); d.put("triggers",triggersField.getText()!=null?triggersField.getText():""); saveToFirestore("behavior_logs",d,"Log saved.",()->{ BehaviorLog l=new BehaviorLog();l.setParentId(currentUser.getUserId());l.setDate(nowDate);l.setBehaviors(b);l.setTriggers(triggersField.getText()); behaviorLogs.add(0,l); behaviourField.clear();triggersField.clear(); }); }
    @FXML void submitFinancialAid() { String d = govtDeptComboBox.getValue(); if (isNullOrEmpty(d)) { showAlert(Alert.AlertType.WARNING,"Input Error","Select department."); return; } if (!isCurrentUserValid()) return; Map<String,Object> a = new HashMap<>(); a.put("parentId",currentUser.getUserId()!=null?currentUser.getUserId():-1L); a.put("parentEmail",currentUser.getEmail()); a.put("govtDepartment",d); a.put("status","Submitted"); a.put("submittedAt",Timestamp.now()); saveToFirestore("financial_applications",a,"Application submitted.",()->{ FinancialApplication fa=new FinancialApplication();fa.setParentId(currentUser.getUserId());fa.setGovtDepartment(d);fa.setStatus("Submitted"); financialApplications.add(0,fa); govtDeptComboBox.getSelectionModel().clearSelection(); }); }
    @FXML void submitQuestion() { String q = questionArea.getText(); if (isNullOrEmpty(q)) { showAlert(Alert.AlertType.WARNING,"Input Error","Question empty."); return; } if (!isCurrentUserValid()) return; Map<String,Object> data = new HashMap<>(); data.put("title",q); data.put("authorId",currentUser.getUserId()!=null?currentUser.getUserId():-1L); data.put("authorEmail",currentUser.getEmail()); data.put("authorName",currentUser.getName()); data.put("timestamp",Timestamp.now()); saveToFirestore("questions",data,"Question posted.",()->{ questionArea.clear(); loadQuestionsFromFirestore(); }); }

    @FXML
    void submitAnswer() {
        QnQuestion selQ = qnaListView.getSelectionModel().getSelectedItem();
        String ans = answerArea.getText();
        if(selQ==null) {showAlert(Alert.AlertType.WARNING,"Select","Select question."); return;}
        if(isNullOrEmpty(ans)) {showAlert(Alert.AlertType.WARNING,"Input Error","Answer empty."); return;}
        if(!isCurrentUserValid()) return;
        if(isNullOrEmpty(selQ.getDocumentId())) {showAlert(Alert.AlertType.ERROR,"Data Error","Invalid Question ID."); return;}

        Map<String,Object> data=new HashMap<>();
        data.put("questionId",selQ.getDocumentId());
        data.put("questionTitle",selQ.getTitle());
        data.put("authorId",currentUser.getUserId()!=null?currentUser.getUserId():-1L);
        data.put("authorEmail",currentUser.getEmail());
        data.put("authorName",currentUser.getName());
        data.put("content",ans);
        data.put("answeredAt",Timestamp.now());

        saveToFirestore("answers",data,"Answer submitted.",()->{
            answerArea.clear();
            loadAnswersForQuestion(selQ);
        });
    }

    @FXML
    void submitComment() {
        Blog selBlog = blogListView.getSelectionModel().getSelectedItem();
        String commentText = commentField.getText();

        if (selBlog == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a blog to comment on."); return;
        }
        if (isNullOrEmpty(commentText)) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Comment cannot be empty."); return;
        }
        if (!isCurrentUserValid()) return;
        if (isNullOrEmpty(selBlog.getDocumentId())) {
            showAlert(Alert.AlertType.ERROR, "Data Error", "Invalid Blog ID."); return;
        }

        Map<String,Object> data = new HashMap<>();
        data.put("blogId", selBlog.getDocumentId());
        data.put("content", commentText);
        data.put("authorId", currentUser.getUserId() != null ? currentUser.getUserId() : -1L);
        data.put("authorEmail", currentUser.getEmail());
        data.put("authorName", currentUser.getName());
        data.put("commentedAt", Timestamp.now());

        saveToFirestore("comments", data, "Comment posted.", () -> {
            commentField.clear();
            loadCommentsForBlog(selBlog); // Reload comments
        });
    }

    // --- MODIFIED: requestConsultation ---
    @FXML
    void requestConsultation() {
        Doctor doc = doctorListView.getSelectionModel().getSelectedItem();
        if (doc == null) {
            showAlert(Alert.AlertType.WARNING,"Selection Error","Select doctor.");
            return;
        }
        if (!isCurrentUserValid()) return;
        if (isNullOrEmpty(doc.getEmail())) {
            showAlert(Alert.AlertType.ERROR,"Request Error","Doctor email missing.");
            return;
        }

        // --- ADDED: Get the checkbox value ---
        boolean dataSharingApproved = dataSharingCheckbox.isSelected();

        Map<String,Object> data = new HashMap<>();
        data.put("parentId",currentUser.getUserId()!=null?currentUser.getUserId():-1L);
        data.put("parentEmail",currentUser.getEmail());
        data.put("parentName",currentUser.getName());
        data.put("doctorId",doc.getUserId());
        data.put("doctorEmail",doc.getEmail());
        data.put("doctorName",doc.getName());
        data.put("status","REQUESTED");
        data.put("requestedAt",Timestamp.now());

        // --- ADDED: Save the consent value ---
        data.put("dataSharingApproved", dataSharingApproved);

        saveToFirestore("consultations",data,"Request sent to Dr. "+doc.getName()+".", () -> {
            // --- ADDED: Clear checkbox after successful request ---
            dataSharingCheckbox.setSelected(false);
        });
    }

    @FXML void handleExportCSV(ActionEvent event) { if (behaviorLogs.isEmpty()){ showAlert(Alert.AlertType.WARNING,"No Data","No logs to export."); return; } if(!isCurrentUserValid()) return; FileChooser fc=new FileChooser(); fc.setTitle("Save Behavior Report"); String safeName=!isNullOrEmpty(currentUser.getName())?currentUser.getName().replaceAll("[^a-zA-Z0-9.-]","_"):"user"; fc.setInitialFileName("behavior_report_"+safeName+".csv"); fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)","*.csv")); Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow(); File file=fc.showSaveDialog(stage); if(file!=null){ String fp=file.getAbsolutePath(); if(!fp.toLowerCase().endsWith(".csv")) file=new File(fp+".csv"); try(PrintWriter pw=new PrintWriter(new FileWriter(file))){ pw.println("Date,Behaviors,Triggers"); for(BehaviorLog log:behaviorLogs){ if(log.getDate()==null)continue; String date=log.getDate(); String b=escapeCsv(log.getBehaviors()); String t=escapeCsv(log.getTriggers()); pw.printf("%s,%s,%s%n",escapeCsv(date),b,t); } showAlert(Alert.AlertType.INFORMATION,"Export OK","Logs exported:\n"+file.getAbsolutePath()); } catch(IOException e){ handleFirestoreError("export CSV",e); } } }
    @FXML void playEmotionGame() { openWebpage("https://pbskids.org/daniel/games/feelings"); }
    @FXML void playStoryGame() { openWebpage("https://pbskids.org/pinkalicious/games/pinkcredible-story-maker"); }
    @FXML void playPatternGame() { openWebpage("https://pbskids.org/games/play/sorting-box/487"); }
    @FXML void handleLogout(ActionEvent event) throws IOException { SessionManager.getInstance().clearSession(); SceneSwitcher.switchScene(event, "home-page.fxml"); }
    @FXML
    void writeNewBlog(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "write-blog.fxml");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Nav Error", "Cannot open blog editor.");
            e.printStackTrace();
        }
    }

    // --- Helpers ---
    private void saveToFirestore(String coll, Map<String,Object> data, String msg, Runnable action) { if (!ThanalApp.isFirebaseInitialized()){ handleFirestoreError("save "+coll,null); return; } try { Firestore db=FirestoreClient.getFirestore(); ApiFuture<WriteResult> f=db.collection(coll).document().set(data); f.addListener(()->{ try { f.get(); System.out.println("Saved to "+coll); Platform.runLater(()->{ if(action!=null)action.run(); if(!isNullOrEmpty(msg))showAlert(Alert.AlertType.INFORMATION,"Success",msg); }); } catch(Exception e){ handleFirestoreError("save "+coll+" result", e); } }, firestoreExecutor); } catch (Exception e) { handleFirestoreError("submit "+coll, e); } }
    private boolean isCurrentUserValid() { if(currentUser==null){showAlert(Alert.AlertType.ERROR,"Action Error","Session invalid.");return false;} if(isNullOrEmpty(currentUser.getEmail())){showAlert(Alert.AlertType.ERROR,"Action Error","Email missing.");return false;} return true; }
    private boolean isCurrentUserValidForQuery() { if(currentUser==null||isNullOrEmpty(currentUser.getEmail())){System.err.println("Cannot query: user/email missing.");return false;} return true; }
    private void handleFirestoreError(String op, Exception e) { String msg=(e!=null)?e.getMessage():"Unknown"; System.err.println("Firestore error ["+op+"]: "+msg); if(e!=null)e.printStackTrace(); Platform.runLater(()->showAlert(Alert.AlertType.ERROR,"Database Error","Op failed: ["+op+"].")); }
    private boolean isNullOrEmpty(String s){ return s==null||s.trim().isEmpty(); }
    private String escapeCsv(String d) { if(d==null)return "\"\""; if(d.contains(",")||d.contains("\"")||d.contains("\n")||d.contains("\r")) return "\""+d.replace("\"","\"\"")+"\""; return d; }
    private void showAlert(Alert.AlertType t, String title, String msg) { Platform.runLater(()->new Alert(t,msg){{setTitle(title);setHeaderText(null);}}.showAndWait()); }
    private void openWebpage(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                showAlert(Alert.AlertType.WARNING, "Error", "Could not open browser.");
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open link: " + e.getMessage());
        }
    }
    private void disableUIComponents() { welcomeLabel.setText("Parent Dashboard - OFFLINE"); if(behaviourField!=null)behaviourField.setDisable(true); if(triggersField!=null)triggersField.setDisable(true); /* ... disable others ... */ }

} // End of ParentDashboardController class