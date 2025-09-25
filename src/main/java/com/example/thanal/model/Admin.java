package com.example.thanal.model;

public class Admin extends User {
    // Methods from UML
    public void verifyUser(User user) {
        // TODO: Implement user verification logic
        System.out.println("Admin is verifying user: " + user.getName());
    }

    public void manageUsers() {
        // TODO: Implement user management logic
        System.out.println("Admin is managing users.");
    }
}