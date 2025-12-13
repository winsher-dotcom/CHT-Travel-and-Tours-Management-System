package com.cht.TravelAndToursManagement.client.viewmodel;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class PackageViewModel {
    private final SimpleStringProperty packageId;
    private final SimpleStringProperty packageName;
    private final SimpleStringProperty description;
    private final SimpleStringProperty destination;
    private final SimpleStringProperty durationDays;
    private final SimpleStringProperty maxParticipants;
    private final SimpleStringProperty inclusions;
    private final SimpleStringProperty price;
    private final SimpleBooleanProperty isActive;

    public PackageViewModel(int packageId, String packageName, String description, String destination, int durationDays, int maxParticipants, String inclusions, double price, boolean isActive) {
        this.packageId = new SimpleStringProperty(String.valueOf(packageId));
        this.packageName = new SimpleStringProperty(packageName);
        this.description = new SimpleStringProperty(description);
        this.destination = new SimpleStringProperty(destination);
        this.durationDays = new SimpleStringProperty(String.valueOf(durationDays));
        this.maxParticipants = new SimpleStringProperty(String.valueOf(maxParticipants));
        this.inclusions = new SimpleStringProperty(inclusions);
        this.price = new SimpleStringProperty(String.valueOf(price));
        this.isActive = new SimpleBooleanProperty(isActive);
    }

    public SimpleStringProperty packageIdProperty() {
        return packageId;
    }

    public SimpleStringProperty packageNameProperty() {
        return packageName;
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleStringProperty destinationProperty() {
        return destination;
    }

    public SimpleStringProperty durationDaysProperty() {
        return durationDays;
    }

    public SimpleStringProperty maxParticipantsProperty() {
        return maxParticipants;
    }

    public SimpleStringProperty inclusionsProperty() {
        return inclusions;
    }

    public SimpleStringProperty priceProperty() {
        return price;
    }

    public SimpleBooleanProperty isActiveProperty() {
        return isActive;
    }

}
