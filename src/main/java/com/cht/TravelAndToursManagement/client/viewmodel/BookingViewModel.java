package com.cht.TravelAndToursManagement.client.viewmodel;

import com.cht.TravelAndToursManagement.client.model.Booking;
import javafx.beans.property.SimpleStringProperty;

public class BookingViewModel {
    private final SimpleStringProperty bookingId;
    private final SimpleStringProperty employeeId;
    private final SimpleStringProperty clientId;
    private final SimpleStringProperty packageId;
    private final SimpleStringProperty bookingDate;
    private final SimpleStringProperty status;
    private final SimpleStringProperty paxCount;

    public BookingViewModel(Booking booking) {
        this.bookingId = new SimpleStringProperty(String.valueOf(booking.getBookingId()));
        this.employeeId = new SimpleStringProperty(String.valueOf(booking.getEmployeeId()));
        this.clientId = new SimpleStringProperty(String.valueOf(booking.getClientId()));
        this.packageId = new SimpleStringProperty(String.valueOf(booking.getPackageId()));
        this.bookingDate = new SimpleStringProperty(booking.getBookingDate());
        this.status = new SimpleStringProperty(booking.getStatus());
        this.paxCount = new SimpleStringProperty(String.valueOf(booking.getPaxCount()));
    }

    public SimpleStringProperty bookingIdProperty() {
        return bookingId;
    }

    public SimpleStringProperty employeeIdProperty() {
        return employeeId;
    }

    public SimpleStringProperty clientIdProperty() {
        return clientId;
    }

    public SimpleStringProperty packageIdProperty() {
        return packageId;
    }

    public SimpleStringProperty bookingDateProperty() {
        return bookingDate;
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public SimpleStringProperty paxCountProperty() {
        return paxCount;
    }


}
