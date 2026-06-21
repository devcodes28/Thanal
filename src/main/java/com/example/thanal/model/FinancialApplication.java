package com.example.thanal.model;

public class FinancialApplication {
    private Long appId;
    private Long parentId;
    private String govtDepartment;
    private String status;

    // Method from UML
    public void submit() {
        // TODO: Logic to submit the application
        System.out.println("Submitting financial application for parent ID: " + parentId);
    }

    // Getters and Setters
    public Long getAppId() { return appId; }
    public void setAppId(Long appId) { this.appId = appId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getGovtDepartment() { return govtDepartment; }
    public void setGovtDepartment(String govtDepartment) { this.govtDepartment = govtDepartment; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}