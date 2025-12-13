package com.cht.TravelAndToursManagement.client.viewmodel;

import com.cht.TravelAndToursManagement.client.model.Employee;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeViewModel {
    private final SimpleStringProperty name;
    private final SimpleStringProperty email;
    private final SimpleStringProperty contactNumber;
    private final SimpleBooleanProperty isManager;
    private final SimpleBooleanProperty isActive;

    public EmployeeViewModel(Employee employee) {
        this.name = new SimpleStringProperty(employee.getName());
        this.email = new SimpleStringProperty(employee.getEmail());
        this.contactNumber = new SimpleStringProperty(employee.getContactNumber());
        this.isManager = new SimpleBooleanProperty(employee.isManager());
        this.isActive = new SimpleBooleanProperty(employee.isActive());
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public SimpleStringProperty contactNumberProperty() {
        return contactNumber;
    }

    public SimpleBooleanProperty isManagerProperty() {
        return isManager;
    }

    public SimpleBooleanProperty isActiveProperty() {
        return isActive;
    }


}
