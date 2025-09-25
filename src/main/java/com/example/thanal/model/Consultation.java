package com.example.thanal.model;

import java.time.LocalDateTime;

public class Consultation {
    private Long consultId;
    private Long parentId;
    private Long doctorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // e.g., "REQUESTED", "ACCEPTED", "COMPLETED"
    private boolean dataSharingApproved;

    // Getters and Setters
    public Long getConsultId() { return consultId; }
    public void setConsultId(Long consultId) { this.consultId = consultId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isDataSharingApproved() { return dataSharingApproved; }
    public void setDataSharingApproved(boolean dataSharingApproved) { this.dataSharingApproved = dataSharingApproved; }
}