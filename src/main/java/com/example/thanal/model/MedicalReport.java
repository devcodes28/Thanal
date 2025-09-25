package com.example.thanal.model;

public class MedicalReport {
    private Long reportId;
    private Long parentId;
    private Long patientId; // Child's ID
    private String filePath;

    // Method from UML
    public void upload() {
        // TODO: Logic to upload the report file
        System.out.println("Uploading medical report from path: " + filePath);
    }

    // Getters and Setters
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}