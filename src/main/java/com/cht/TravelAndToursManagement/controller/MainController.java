package com.cht.TravelAndToursManagement.controller;

import com.cht.TravelAndToursManagement.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static com.cht.TravelAndToursManagement.App.stage;

public class MainController {


    public static void changeScene(String fxml) throws IOException {
        // Load the new FXML file
        Parent root = FXMLLoader.load(App.class.getResource(fxml));
        stage.setScene(new Scene(root, 800, 600));
        stage.centerOnScreen();
        stage.setTitle("Travel and Tours Management System");

    }

}
