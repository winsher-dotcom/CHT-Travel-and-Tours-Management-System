package com.cht.TravelAndToursManagement.client.controller;

import com.cht.TravelAndToursManagement.client.ClientApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;


import java.io.IOException;
import java.util.Objects;

public class SceneChanger {
    public SceneChanger(StackPane currentView, String fxml) throws IOException {
        Pane nextView = FXMLLoader.load(Objects.requireNonNull(ClientApp.class.getResource(fxml)));

        currentView.getChildren().setAll(nextView);

    }

    public SceneChanger(BorderPane currentView, String fxml) throws IOException {
        BorderPane nextView = FXMLLoader.load(Objects.requireNonNull(ClientApp.class.getResource(fxml)));
        currentView.getChildren().removeAll();
        currentView.setCenter(nextView);
    }


}
