package com.cht.TravelAndToursManagement.client.utils;

import javafx.scene.control.Alert;

public class ErrorDialog {
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
