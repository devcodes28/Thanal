package com.example.thanal.model;
import java.util.List;
public abstract class User {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String password;
    private List<QnQuestion> questions;
    private List<QnAnswer> answers;
    // Methods
    public boolean register() {
        System.out.println("Registering user: " + this.name);
        return true;
    }
    public boolean login() {
        System.out.println("Logging in user: " + this.email);
        return true;
    }
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<QnQuestion> getQuestions() { return questions; }
    public void setQuestions(List<QnQuestion> questions) { this.questions = questions; }
    public List<QnAnswer> getAnswers() { return answers; }
    public void setAnswers(List<QnAnswer> answers) { this.answers = answers; }
}