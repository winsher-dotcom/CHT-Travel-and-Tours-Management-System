package com.cht.TravelAndToursManagement.client.controller;

import com.cht.TravelAndToursManagement.client.config.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.mysql.cj.conf.PropertyKey.logger;

public class MainLayoutController extends SceneController implements Initializable {

    @FXML
    public Label totalCustomer;
    @FXML
    public Label ongoingTrips;
    @FXML
    public Label upcomingTrips;
    @FXML
    public Label completedTrips;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayTotalCustomers();
        displayOngoingTrips();
        displayUpcomingTrips();
        displayCompletedTrips();

    }


    @FXML
    public void addBooking() throws IOException {
        setCenter("/com/cht/TravelAndToursManagement/view/AddBooking1-view.fxml");

    }


    @FXML
    public void addBookingStep2() throws IOException {
        setCenter("/com/cht/TravelAndToursManagement/view/AddBooking2-view.fxml");
    }

    @FXML
    public void goToDashboard() throws IOException {

        setCenter("/com/cht/TravelAndToursManagement/view/MainLayout-view.fxml");
    }

    @FXML
    public void goToDashboard2() throws IOException {

        setCenter("/com/cht/TravelAndToursManagement/view/MainLayout-view2.fxml");
    }

    @FXML
    public void goToEmployee() {
        setCenter("/com/cht/TravelAndToursManagement/view/Employee-view.fxml");
    }

    //    display the total number of customers in the dashboard
    public void displayTotalCustomers() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String customerCountQuery = "SELECT COUNT(*) AS total FROM client";

        try {
            var statement = connectDB.createStatement();
            var queryResult = statement.executeQuery(customerCountQuery);

            while (queryResult.next()) {
                int totalCustomers = queryResult.getInt("total");
                totalCustomer.setText(String.valueOf(totalCustomers));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    display the ongoing trips in the dashboard
    public void displayOngoingTrips() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String ongoingTripsQuery = "SELECT COUNT(*) AS ongoing FROM booking WHERE status = 'pending'";

        try {
            var statement = connectDB.createStatement();
            var queryResult = statement.executeQuery(ongoingTripsQuery);

            while (queryResult.next()) {
                int ongoingTripsCount = queryResult.getInt("ongoing");
                ongoingTrips.setText(String.valueOf(ongoingTripsCount));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //   display the upcoming trips in the dashboard
    public void displayUpcomingTrips() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String upcomingTripsQuery = "SELECT COUNT(*) AS upcoming FROM booking WHERE status = 'pending'";

        try {
            var statement = connectDB.createStatement();
            var queryResult = statement.executeQuery(upcomingTripsQuery);

            while (queryResult.next()) {
                int upcomingTripsCount = queryResult.getInt("upcoming");
                upcomingTrips.setText(String.valueOf(upcomingTripsCount));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // display the completed trips in the dashboard
    public void displayCompletedTrips() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String completedTripsQuery = "SELECT COUNT(*) AS completed FROM booking WHERE status = 'confirmed'";

        try {
            var statement = connectDB.createStatement();
            var queryResult = statement.executeQuery(completedTripsQuery);

            while (queryResult.next()) {
                int completedTripsCount = queryResult.getInt("completed");
                completedTrips.setText(String.valueOf(completedTripsCount));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

