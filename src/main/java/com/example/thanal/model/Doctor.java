package com.example.thanal.model;

import java.util.Collections;
import java.util.List;

public class Doctor extends User {
    private String licenseNo;
    private String specialization;
    private List<MedicalReport> writtenReports;
    private List<Blog> blogs;
    private List<Consultation> managedConsultations;

    // Methods
    public List<Object> viewAppointments() {
        System.out.println("Doctor " + getName() + " is viewing appointments.");
        return Collections.emptyList();
    }
    public void acceptConsultation(Consultation request) {
        System.out.println("Doctor " + getName() + " accepted a consultation.");
    }
    public void requestBehaviorAccess(Consultation consult) {
        System.out.println("Doctor " + getName() + " is requesting access to behavior logs.");
    }

    // Getters and Setters
    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public List<MedicalReport> getWrittenReports() { return writtenReports; }
    public void setWrittenReports(List<MedicalReport> writtenReports) { this.writtenReports = writtenReports; }
    public List<Blog> getBlogs() { return blogs; }
    public void setBlogs(List<Blog> blogs) { this.blogs = blogs; }
    public List<Consultation> getManagedConsultations() { return managedConsultations; }
    public void setManagedConsultations(List<Consultation> managedConsultations) { this.managedConsultations = managedConsultations; }
}