package com.example.thanal.model;

import java.io.File;
import java.time.LocalDate;
public class BehaviorLog {
    private Long logId;
    private Long parentId;
    private LocalDate date;
    private String behaviors;
    private String triggers;

    public File exportCSV() {
        // TODO: Logic to export behavior logs to a CSV file
        System.out.println("Exporting behavior logs to CSV.");
        return null;
    }
    // Getters and Setters
    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getBehaviors() { return behaviors; }
    public void setBehaviors(String behaviors) { this.behaviors = behaviors; }
    public String getTriggers() { return triggers; }
    public void setTriggers(String triggers) { this.triggers = triggers; }
}