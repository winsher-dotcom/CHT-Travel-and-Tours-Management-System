module com.cht.TravelAndToursManagement {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.cht.TravelAndToursManagement to javafx.fxml;
    exports com.cht.TravelAndToursManagement;
}
