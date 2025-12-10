package com.cht.TravelAndToursManagement.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class ClientApp extends Application {


    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/cht/TravelAndToursManagement/view/Login-view.fxml")));
//        Parent root = fxmlLoader.load();

//        SceneManager manager = SceneManager.getInstance();
//        manager.setStage(stage);
//
//        manager.switchScene("/com/cht/TravelAndToursManagement/view/Login-view.fxml");
////        Scene scene = new Scene(root, 100, 800);
//        stage.setTitle("Travel and Tours Management System");
//        stage.setMaximized(true);
////        stage.setScene(scene);
//        stage.centerOnScreen();
//        stage.show();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cht/TravelAndToursManagement/view/Login-view.fxml"));
        Parent loginRoot = loader.load();

        Scene loginScene = new Scene(loginRoot, 400, 600);
        stage.setScene(loginScene);
        stage.setTitle("Login - Travel and Tours Management");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}