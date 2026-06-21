package com.example.thanal.model;

import com.google.cloud.Timestamp;

public class Comment {
    private String blogId;
    private String content;
    private String authorName;
    private String authorEmail;
    private Timestamp commentedAt;

    public String getBlogId() { return blogId; }
    public void setBlogId(String blogId) { this.blogId = blogId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAuthorEmail() { return authorEmail; }
    public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }
    public Timestamp getCommentedAt() { return commentedAt; }
    public void setCommentedAt(Timestamp commentedAt) { this.commentedAt = commentedAt; }
}