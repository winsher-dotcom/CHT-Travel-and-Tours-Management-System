    package com.cht.TravelAndToursManagement;

    import javafx.application.Application;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Button;
    import javafx.stage.Stage;

    import java.io.IOException;
    import java.util.Objects;


    public class App extends Application {


        public static Stage stage;

        @Override
        public void start(Stage primaryStage) throws IOException {

            App.stage = primaryStage;

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/login.fxml")));

            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.setTitle("Travel and Tours Management System");
            primaryStage.show();



        }







        public static void main(String[] args) {
            launch();
        }



    }