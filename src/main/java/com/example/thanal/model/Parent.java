package com.example.thanal.model;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class Parent extends User {
    private String address;
    private String childName;
    private LocalDate childDob;

    // Relationships from the diagram
    private List<BehaviorLog> logs;
    private List<FinancialApplication> financialApplications;
    private List<MedicalReport> medicalReports;
    private Consultation consultation; // 1-to-1 relationship with initiated consultation

    // Methods from UML
    public void uploadMedicalReport(File file) {
        // TODO: Implement file upload logic
        System.out.println("Parent " + getName() + " is uploading a medical report.");
    }

    public File downloadBehaviorReport() {
        // TODO: Implement report generation and download logic
        System.out.println("Parent " + getName() + " is downloading a behavior report.");
        return null;
    }

    public void approveDataSharing(Doctor doctor, String period) {
        // TODO: Implement data sharing approval logic
        System.out.println("Parent " + getName() + " approved data sharing with Dr. " + doctor.getName());
    }

    // Getters and Setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }
    public LocalDate getChildDob() { return childDob; }
    public void setChildDob(LocalDate childDob) { this.childDob = childDob; }
    public List<BehaviorLog> getLogs() { return logs; }
    public void setLogs(List<BehaviorLog> logs) { this.logs = logs; }
    public List<FinancialApplication> getFinancialApplications() { return financialApplications; }
    public void setFinancialApplications(List<FinancialApplication> financialApplications) { this.financialApplications = financialApplications; }
    public List<MedicalReport> getMedicalReports() { return medicalReports; }
    public void setMedicalReports(List<MedicalReport> medicalReports) { this.medicalReports = medicalReports; }
    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }
}