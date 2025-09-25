package com.example.thanal.model;

public class QnQuestion {
    private Long questionId;
    private Long authorId; // Corresponds to User's userId
    private String content;
    private String title; // A title field is useful, though not in UML

    // Method from UML
    public void post() {
        // TODO: Implement logic to post the question
        System.out.println("Posting question: " + title);
    }

    // Getters and Setters
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}