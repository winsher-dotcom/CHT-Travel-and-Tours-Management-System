package com.cht.TravelAndToursManagement.client.controller;


import com.cht.TravelAndToursManagement.client.config.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

public class AuthController extends SceneController {
    // Db Connection
    DatabaseConnection connectNow = new DatabaseConnection();

    @FXML
    private Button createAccountButton;
    @FXML
    private Label registerMessageLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameTextField;
    @FXML
    private Label emailLabel;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField contactNumberTextField;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private BorderPane loginContainer;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;


    public void loginButton() {
        loginMessageLabel.setText("You try to Login!");

        if (!usernameTextField.getText().isBlank() && !passwordPasswordField.getText().isBlank()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Username or Password is empty!");
        }
        // Stage stage = (Stage) loginContainer.getScene().getWindow();
        // stage.setMaximized(true);

    }

    public void cancelButton() throws IOException {
        Stage stage = (Stage) loginContainer.getScene().getWindow();
        stage.close();

    }

    public void validateLogin() {
        String email = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        String verifyLogin = "SELECT COUNT(1) FROM Employee WHERE Email = ? AND Password = ?";

        try (Connection connectDB = connectNow.getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLogin);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            while (resultSet.next()) {
                if (resultSet.getInt(1) == 1) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cht/TravelAndToursManagement/view/MainLayout-view.fxml"));
                        BorderPane mainRoot = loader.load();
                        Scene mainScene = new Scene(mainRoot, 1200, 800);

                        Stage stage = (Stage) loginButton.getScene().getWindow(); // reuse stage
                        stage.centerOnScreen();
                        stage.setMaximized(true);
                        stage.setScene(mainScene);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    loginMessageLabel.setText("Login Successfully!");
                } else {
                    loginMessageLabel.setText("Invalid Login. Please try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerEmployee() {
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String contactNumber = contactNumberTextField.getText();
        String password = confirmPasswordField.getText();

        String insertEmployee = "INSERT INTO Employee (name, email, contactNumber, password, isManager, isActive) VALUES (?, ?, ?, ?, ?, ?)";


        try (Connection connectDB = connectNow.getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(insertEmployee)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, contactNumber);
            preparedStatement.setString(4, password);
            preparedStatement.setBoolean(5, false);
            preparedStatement.setBoolean(6, true);

            registerMessageLabel.setText("Account Created Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            registerMessageLabel.setText("Error Creating Account!");
        }

    }

    public void createAccountButton(ActionEvent event) {
        setCenter("/com/cht/TravelAndToursManagement/view/Register-view.fxml");
    }


}
