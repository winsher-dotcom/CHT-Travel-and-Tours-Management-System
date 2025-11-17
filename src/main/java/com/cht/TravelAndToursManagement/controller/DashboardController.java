package com.cht.TravelAndToursManagement.controller;

import javafx.event.ActionEvent;

import java.io.IOException;

public class DashboardController {
    public void addBooking(ActionEvent actionEvent) throws IOException {
        MainController.changeScene("view/AddBooking.fxml");

    }
    public void homeButton(ActionEvent actionEvent) throws IOException {
        MainController.changeScene("view/Home.fxml");

    }
}
