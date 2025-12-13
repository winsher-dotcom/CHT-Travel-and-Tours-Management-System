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

public class MainLayoutController extends ErrorDialog {
    public static final Logger logger = LoggerFactory.getLogger(MainLayoutController.class);

    private final DashboardService dashboardService;

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

    private final NavigationService navigationService;

    public MainLayoutController(DashboardService dashboardService, NavigationService navigationService) {
        this.dashboardService = dashboardService;
        this.navigationService = navigationService;
    }

    /**
     * Swap the center content of the main layout while keeping the sidebar intact.
     */
    private void setCenterContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Node node = loader.load();
            if (contentArea != null) {
                contentArea.setCenter(node);
            }
        } catch (IOException e) {
            logger.error("Failed to load center content FXML: {}", fxmlPath, e);
            showError("Failed to load content");
        }
    }

    @FXML
    public void goToEmployee() {
        // Load Employee center content inside the existing main layout (sidebar preserved)
        setCenterContent(FXMLPaths.EMPLOYEE);
    }

    @FXML
    public void goToBookingView() {
        // Load Booking center content inside the existing main layout (sidebar preserved)

        setCenterContent(FXMLPaths.BOOKING);


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


    public void addBooking(ActionEvent actionEvent) {
        // Load first booking step inside the existing main layout (sidebar preserved)
        setCenterContent(FXMLPaths.BOOKING_STEP1);


    }


}
