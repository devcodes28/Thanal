package com.example.thanal.model;

public class Blog {
    private Long blogId;
    private String title;
    private String content;
    private Long authorId;
    private float rating;
    // Methods
    public void create() {
        System.out.println("Creating blog: " + title);
    }
    public void edit() {
        System.out.println("Editing blog: " + title);
    }
    // Getters and Setters
    public Long getBlogId() { return blogId; }
    public void setBlogId(Long blogId) { this.blogId = blogId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
}