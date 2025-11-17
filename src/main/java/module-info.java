module com.cht.TravelAndToursManagement {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.cht.TravelAndToursManagement.controller to javafx.fxml;
    exports com.cht.TravelAndToursManagement;
}
