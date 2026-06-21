package com.example.thanal.model;

import com.google.cloud.Timestamp;

public class Consultation {
    private String consultId;
    private Long parentId;
    private Long doctorId;
    private String status;
    private boolean dataSharingApproved;
    private String parentName;
    private String parentEmail;
    private String doctorEmail; // Added missing field
    private String doctorName;  // Added missing field


    // --- ADDED Timestamp Fields (Match Firestore) ---
    private Timestamp requestedAt;
    private Timestamp lastUpdatedAt;


    // Getters and Setters
    public String getConsultId() { return consultId; }
    public void setConsultId(String consultId) { this.consultId = consultId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isDataSharingApproved() { return dataSharingApproved; }
    public void setDataSharingApproved(boolean dataSharingApproved) { this.dataSharingApproved = dataSharingApproved; }
    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }
    public String getParentEmail() { return parentEmail; }
    public void setParentEmail(String parentEmail) { this.parentEmail = parentEmail; }
    public String getDoctorEmail() { return doctorEmail; } // Added getter/setter
    public void setDoctorEmail(String doctorEmail) { this.doctorEmail = doctorEmail; } // Added getter/setter
    public String getDoctorName() { return doctorName; } // Added getter/setter
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; } // Added getter/setter


    public Timestamp getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Timestamp requestedAt) { this.requestedAt = requestedAt; }
    public Timestamp getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(Timestamp lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
}