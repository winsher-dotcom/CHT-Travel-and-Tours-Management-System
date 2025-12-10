package com.cht.TravelAndToursManagement.client.controller;

import com.cht.TravelAndToursManagement.client.config.DatabaseConfig;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

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
        String customerCountQuery = "SELECT COUNT(*) AS total FROM client";

        try (Connection connectDB = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(customerCountQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int totalCustomers = resultSet.getInt("total");
                totalCustomer.setText(String.valueOf(totalCustomers));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    display the ongoing trips in the dashboard
    public void displayOngoingTrips() {

        String ongoingTripsQuery = "SELECT COUNT(*) AS ongoing FROM booking WHERE status = 'pending'";

        try (Connection connectDB = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(ongoingTripsQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int ongoingTripsCount = resultSet.getInt("ongoing");
                ongoingTrips.setText(String.valueOf(ongoingTripsCount));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //   display the upcoming trips in the dashboard
    public void displayUpcomingTrips() {
        String upcomingTripsQuery = "SELECT COUNT(*) AS upcoming FROM booking WHERE status = 'pending'";

        try (Connection connectDB = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(upcomingTripsQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int upcomingTripsCount = resultSet.getInt("upcoming");
                upcomingTrips.setText(String.valueOf(upcomingTripsCount));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // display the completed trips in the dashboard
    public void displayCompletedTrips() {

        String completedTripsQuery = "SELECT COUNT(*) AS completed FROM booking WHERE status = 'confirmed'";

        try (Connection connectDB = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(completedTripsQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int completedTripsCount = resultSet.getInt("completed");
                completedTrips.setText(String.valueOf(completedTripsCount));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

