module com.cht.TravelAndToursManagement.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.j;
    requires jdk.httpserver;
    requires org.slf4j;
    requires com.zaxxer.hikari;
    requires java.desktop;

    opens com.cht.TravelAndToursManagement.client.controller to javafx.fxml;
    exports com.cht.TravelAndToursManagement.client;
    opens com.cht.TravelAndToursManagement.client.utils to javafx.fxml;
//    opens com.cht.TravelAndToursManagement.client.controller to javafx.fxml;
//    exports com.cht.TravelAndToursManagement.client;
}
