package com.example.thanal.model;

import java.util.List;

/**
 * Represents the base User entity as per the UML diagram.
 * This is an abstract class because a user must be a specific type (Parent, Doctor, etc.).
 */
public abstract class User {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String role;
    // Password should be handled securely, but we include it as a field for structure.
    private String password;

    // Relationships from the diagram
    private List<QnQuestion> questions;
    private List<QnAnswer> answers;

    // Constructors
    public User() {
    }

    public User(Long userId, String name, String email, String phone, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    // Methods from UML
    public boolean register() {
        // TODO: Implement registration logic (e.g., save to a database)
        System.out.println("Registering user: " + this.name);
        return true;
    }

    public boolean login() {
        // TODO: Implement login logic (e.g., check credentials against a database)
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