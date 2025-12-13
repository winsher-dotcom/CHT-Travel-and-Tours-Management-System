package com.cht.TravelAndToursManagement.client.model;

public class Package {
    private int packageID;
    private String packageName;
    private String description;
    private String destination;
    private int durationDays;
    private int maxParticipants;
    private String inclusions;
    private double price;
    private boolean isActive;
    private int createdByEmployeeID;

    public Package(int packageID, String packageName, String description, String destination, int durationDays, int maxParticipants, String inclusions, double price, boolean isActive, int createdByEmployeeID) {
        this.packageID = packageID;
        this.packageName = packageName;
        this.description = description;
        this.destination = destination;
        this.durationDays = durationDays;
        this.maxParticipants = maxParticipants;
        this.inclusions = inclusions;
        this.price = price;
        this.isActive = isActive;
        this.createdByEmployeeID = createdByEmployeeID;
    }

    public int getPackageID() {
        return packageID;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getDescription() {
        return description;
    }

    public String getDestination() {
        return destination;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public String getInclusions() {
        return inclusions;
    }

    public double getPrice() {
        return price;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getCreatedByEmployeeID() {
        return createdByEmployeeID;
    }

    public void setPackageID(int packageID) {
        this.packageID = packageID;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void setInclusions(String inclusions) {
        this.inclusions = inclusions;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setCreatedByEmployeeID(int createdByEmployeeID) {
        this.createdByEmployeeID = createdByEmployeeID;
    }
}
