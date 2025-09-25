package com.example.thanal.model;

public class Admin extends User {
    // Methods
    public void verifyUser(User user) {
        System.out.println("Admin is verifying user: " + user.getName());
    }
    public void manageUsers() {
        System.out.println("Admin is managing users.");
    }
}