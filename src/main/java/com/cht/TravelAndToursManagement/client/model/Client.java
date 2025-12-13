package com.cht.TravelAndToursManagement.client.model;

public class Client {
    private int clientId;
    private String name;
    private String email;
    private String address;
    private String contactNumber;
    private String customerType;
    private String dateRegistered;

    public Client(int clientId, String name, String email, String address, String contactNumber, String customerType, String dateRegistered) {
        this.clientId = clientId;
        this.name = name;
        this.email = email;
        this.address = address;
        this.contactNumber = contactNumber;
        this.customerType = customerType;
        this.dateRegistered = dateRegistered;
    }

    public int getClientId() {
        return clientId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getCustomerType() {
        return customerType;
    }

    public String getDateRegistered() {
        return dateRegistered;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public void setDateRegistered(String dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

}
