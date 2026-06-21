package com.example.thanal.model;

import java.io.File;
// import java.time.LocalDate; // <-- Removed this import
import java.util.List;

public class Parent extends User {
    private String address;
    private String childName;
    private String childDob; // <-- FIXED: Was LocalDate
    private List<BehaviorLog> logs;
    private List<FinancialApplication> financialApplications;
    private List<MedicalReport> medicalReports;
    private Consultation consultation;

    // Methods
    public void uploadMedicalReport(File file) {
        System.out.println("Parent " + getName() + " is uploading a medical report.");
    }

    public File downloadBehaviorReport() {
        System.out.println("Parent " + getName() + " is downloading a behavior report.");
        return null;
    }

    public void approveDataSharing(Doctor doctor, String period) {
        System.out.println("Parent " + getName() + " approved data sharing with Dr. " + doctor.getName());
    }

    // Getters and Setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }
    public String getChildDob() { return childDob; }
    public void setChildDob(String childDob) { this.childDob = childDob; }

    public List<BehaviorLog> getLogs() { return logs; }
    public void setLogs(List<BehaviorLog> logs) { this.logs = logs; }
    public List<FinancialApplication> getFinancialApplications() { return financialApplications; }
    public void setFinancialApplications(List<FinancialApplication> financialApplications) { this.financialApplications = financialApplications; }
    public List<MedicalReport> getMedicalReports() { return medicalReports; }
    public void setMedicalReports(List<MedicalReport> medicalReports) { this.medicalReports = medicalReports; }
    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }
}