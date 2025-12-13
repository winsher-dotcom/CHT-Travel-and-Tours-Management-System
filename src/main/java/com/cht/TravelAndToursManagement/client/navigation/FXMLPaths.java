package com.cht.TravelAndToursManagement.client.navigation;

public final class FXMLPaths {
    private FXMLPaths() {
    }

    // Main layout shell that contains the sidebar and center content area
    public static final String MAIN_LAYOUT = "/com/cht/TravelAndToursManagement/view/MainLayout-view.fxml";

    // Route mappings
    public static final String LOGIN = "/com/cht/TravelAndToursManagement/view/Login-view.fxml";
    public static final String DASHBOARD = MAIN_LAYOUT; // dashboard uses main layout shell
    public static final String EMPLOYEE = "/com/cht/TravelAndToursManagement/view/Employee-view.fxml";
    public static final String REGISTER = "/com/cht/TravelAndToursManagement/view/Register-view.fxml";
    // Treat booking as starting at step 1 for top-level navigation
    public static final String BOOKING_STEP1 = "/com/cht/TravelAndToursManagement/view/AddBooking1-view.fxml";


    public enum BookingStatus {
        PENDING("pending"),
        CONFIRMED("confirmed"),
        CANCELLED("cancelled");

        private final String value;

        BookingStatus(String values) {
            this.value = values;
        }

        public String getValue() {
            return value;
        }
    }
}
