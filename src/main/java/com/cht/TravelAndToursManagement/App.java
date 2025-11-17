package com.cht.TravelAndToursManagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class App extends Application {

	
	
    @Override
    public void start(Stage stage) throws IOException {
        new FXMLLoader();
		Parent root = FXMLLoader.load(getClass().getResource("view/login.fxml"));
        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Travel and Tours Management System");
        stage.setResizable(false);
        stage.show();
        
    }

  

    public static void main(String[] args) {
        launch();
    }

}