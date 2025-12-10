package com.cht.TravelAndToursManagement.client.navigation;

public final class FXMLPaths {
    private FXMLPaths() {
    }

    public static final String LOGIN = "/com/cht/TravelAndToursManagement/view/Login-view.fxml";
    public static final String DASHBOARD = "/com/cht/TravelAndToursManagement/view/MainLayout-view.fxml";
    public static final String EMPLOYEE = "/com/cht/TravelAndToursManagement/view/Employee-view.fxml";
    public static final String REGISTER = "/com/cht/TravelAndToursManagement/view/Register-view.fxml";

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
