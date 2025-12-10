package com.cht.TravelAndToursManagement.client.model;

public class Employee {
    private int employeeId;
    private String name;
    private String email;
    private String contactNumber;
    private boolean isManager;
    private boolean isActive;

    // Constructor, getters, setter
    // No JavaFX dependencies
    public Employee(int employeeId, String name, String email, String contactNumber, boolean isManager, boolean isActive) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.isManager = isManager;
        this.isActive = isActive;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public boolean isManager() {
        return isManager;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


}
