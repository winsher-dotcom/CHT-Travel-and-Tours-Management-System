package com.cht.TravelAndToursManagement.client.viewmodel;

import javafx.beans.property.SimpleStringProperty;

public class ClientViewModel {
    private final SimpleStringProperty clientId;
    private final SimpleStringProperty name;
    private final SimpleStringProperty email;
    private final SimpleStringProperty address;
    private final SimpleStringProperty contactNumber;
    private final SimpleStringProperty customerType;
    private final SimpleStringProperty dateRegistered;

    public ClientViewModel(int clientId, String name, String email, String address, String contactNumber, String customerType, String dateRegistered) {
        this.clientId = new SimpleStringProperty(String.valueOf(clientId));
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.address = new SimpleStringProperty(address);
        this.contactNumber = new SimpleStringProperty(contactNumber);
        this.customerType = new SimpleStringProperty(customerType);
        this.dateRegistered = new SimpleStringProperty(dateRegistered);
    }

    public SimpleStringProperty clientIdProperty() {
        return clientId;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public SimpleStringProperty contactNumberProperty() {
        return contactNumber;
    }

    public SimpleStringProperty customerTypeProperty() {
        return customerType;
    }

    public SimpleStringProperty dateRegisteredProperty() {
        return dateRegistered;
    }


}
