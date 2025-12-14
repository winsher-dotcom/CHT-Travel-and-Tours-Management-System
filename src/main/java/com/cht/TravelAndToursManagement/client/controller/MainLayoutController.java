package com.cht.TravelAndToursManagement.client.controller;

import com.cht.TravelAndToursManagement.client.navigation.FXMLPaths;
import com.cht.TravelAndToursManagement.client.navigation.NavigationService;
import com.cht.TravelAndToursManagement.client.navigation.Route;
import com.cht.TravelAndToursManagement.client.service.DashboardService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainLayoutController {
    public static final Logger logger = LoggerFactory.getLogger(MainLayoutController.class);

    protected final DashboardService dashboardService;
    protected final NavigationService navigationService;


    // Root BorderPane from MainLayout-view.fxml (sidebar + center area)
    @FXML
    private BorderPane contentArea;

    @FXML
    public Label totalCustomer;
    @FXML
    public Label ongoingTrips;
    @FXML
    public Label upcomingTrips;
    @FXML
    public Label completedTrips;

    @FXML
    public Button addBookingButton;


    public MainLayoutController(DashboardService dashboardService, NavigationService navigationService) {
        this.dashboardService = dashboardService;
        this.navigationService = navigationService;
    }

    @FXML
    public void goToEmployee() {
        // Load Employee center content inside the existing main layout (sidebar preserved)
        navigationService.setCenterContent(FXMLPaths.EMPLOYEE);
    }

    @FXML
    public void goToBookingView() {
        // Load Booking center content inside the existing main layout (sidebar preserved)

        navigationService.setCenterContent(FXMLPaths.BOOKING);
    }

    @FXML
    private BorderPane root;

    @FXML
    private VBox titleBox;

    @FXML
    private VBox profileBox;

    private boolean collapsed = false;

    @FXML
    private void toggleSidebar() {
        collapsed = !collapsed;


        if (collapsed) {
            root.setPrefWidth(80);
            titleBox.setVisible(false);
            profileBox.setVisible(false);
        } else {
            root.setPrefWidth(280);
            titleBox.setVisible(true);
            profileBox.setVisible(true);
        }
    }


}
