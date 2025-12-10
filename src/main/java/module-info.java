module com.cht.TravelAndToursManagement.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.j;
    requires jdk.httpserver;

    opens com.cht.TravelAndToursManagement.client.controller to javafx.fxml;
    exports com.cht.TravelAndToursManagement.client;
//    opens com.cht.TravelAndToursManagement.client.controller to javafx.fxml;
//    exports com.cht.TravelAndToursManagement.client;
}
