package com.cht.TravelAndToursManagement.client.controller;

import com.cht.TravelAndToursManagement.client.config.DatabaseConfig;
import com.cht.TravelAndToursManagement.client.navigation.NavigationService;
import com.cht.TravelAndToursManagement.client.navigation.Route;
import com.cht.TravelAndToursManagement.client.service.DashboardService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class MainLayoutController extends SceneController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainLayoutController.class);

    private final DashboardService dashboardService;
    private final NavigationService navigationService;

    @FXML
    public Label totalCustomer;
    @FXML
    public Label ongoingTrips;
    @FXML
    public Label upcomingTrips;
    @FXML
    public Label completedTrips;

    public MainLayoutController(DashboardService dashboardService, NavigationService navigationService) {
        this.dashboardService = dashboardService;
        this.navigationService = navigationService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDashboardStats();

    }


    private void loadDashboardStats() {
        Task<DashboardStats> statsTask = new Task<>() {
            @Override
            protected DashboardStats call() {
                return dashboardService.getDashboardStats();
            }
        };
        statsTask.setOnSucceeded(event -> {
            DashboardStats stats = statsTask.getValue();
            totalCustomer.setText(String.valueOf(stats.totalCustomers()));
            ongoingTrips.setText(String.valueOf(stats.ongoingTrips()));
            upcomingTrips.setText(String.valueOf(stats.upcomingTrips()));
            completedTrips.setText(String.valueOf(stats.completedTrips()));
        });
        statsTask.setOnFailed(event -> {
            logger.error("Failed to load dashboard stats", statsTask.getException());
            showError("Failed to load dashboard data");
        });
        new Thread(statsTask).start();
    }

    @FXML
    public void goToEmployee() {
        navigationService.navigateTo(Route.EMPLOYEE);
    }

    @FXML
    public void addBooking() {
        navigationService.navigateTo(Route.BOOKING);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}

