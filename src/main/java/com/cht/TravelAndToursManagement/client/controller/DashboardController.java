package com.cht.TravelAndToursManagement.client.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class DashboardController {
    @FXML
    public BorderPane DashboardContainer;
    @FXML
    public BorderPane addBookingContainer;
    @FXML
    public BorderPane homeContainer;


    @FXML
    public void addBooking() throws IOException {
        new SceneChanger(DashboardContainer, "view/AddBooking.fxml");

    }

    @FXML
    public void homeButton() throws IOException {
        new SceneChanger(DashboardContainer, "view/Home.fxml");

    }
}
