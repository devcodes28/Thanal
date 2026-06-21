package com.example.thanal.model;

import java.io.File;

public class BehaviorLog {
    private Long logId;
    private Long parentId;
    private String date;
    private String behaviors;
    private String triggers;

    public File exportCSV() {
        System.out.println("Exporting behavior logs to CSV.");
        return null;
    }

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getBehaviors() { return behaviors; }
    public void setBehaviors(String behaviors) { this.behaviors = behaviors; }
    public String getTriggers() { return triggers; }
    public void setTriggers(String triggers) { this.triggers = triggers; }
}