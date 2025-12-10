package com.cht.TravelAndToursManagement.client.model;

import javafx.beans.property.SimpleStringProperty;

public class Employee {
    private String employeeID;
    private String name;
    private String email;
    private String password;
    private String contactNumber;
    private boolean isManager;
    private boolean isActive;

    public Employee(String employeeID, String name, String email, String password, String contactNumber, boolean isManager, boolean isActive) {
        this.employeeID = employeeID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.isManager = isManager;
        this.isActive = isActive;
    }

    // Getters
    public String getEmployeeID() {
        return employeeID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
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

    // Setters
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }


}
