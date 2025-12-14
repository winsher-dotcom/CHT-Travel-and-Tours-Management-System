package com.cht.TravelAndToursManagement.client.controller;

import com.cht.TravelAndToursManagement.client.navigation.ControllerFactory;
import com.cht.TravelAndToursManagement.client.navigation.FXMLPaths;
import com.cht.TravelAndToursManagement.client.navigation.NavigationService;
import com.cht.TravelAndToursManagement.client.service.DashboardService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BookingController extends MainLayoutController implements Initializable {

    @FXML
    private Button addBookingButton;

    private final ControllerFactory controllerFactory;


    public BookingController(DashboardService dashboardService, NavigationService navigationService, ControllerFactory controllerFactory) {
        super(dashboardService, navigationService);
        this.controllerFactory = controllerFactory;
    }


    public void addBooking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPaths.BOOKING_STEP1));
            loader.setControllerFactory(controllerFactory);
            Parent modalRoot = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("Add New Booking");


            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(modalRoot));
            modalStage.centerOnScreen();
            modalStage.setMinWidth(1000);
            modalStage.setMinHeight(800);

            Stage owner = (Stage) addBookingButton.getScene().getWindow();
            modalStage.initOwner(owner);

            modalStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addBookingButton.setOnAction(actionEvent -> {
            addBooking();
        });


    }

}
