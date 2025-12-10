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
    public Button createAccountButton;
    public Label registerMessageLabel;
    public Label nameLabel;
    public TextField nameTextField;
    public Label emailLabel;
    public TextField emailTextField;
    public TextField contactNumberTextField;
    public Label passwordLabel;
    public Label confirmPasswordLabel;
    public PasswordField confirmPasswordField;
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
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String email = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        String verifyLogin = "SELECT COUNT(1) FROM Employee WHERE Email = ? AND Password = ?";

        try {
            PreparedStatement pstmt = connectDB.prepareStatement(verifyLogin);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet queryResult = pstmt.executeQuery(verifyLogin);

            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1) {
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

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String contactNumber = contactNumberTextField.getText();
        String password = confirmPasswordField.getText();

        String insertEmployee = "INSERT INTO Employee (name, email, contactNumber, password, isManager, isActive) VALUES (?, ?, ?, ?, ?, ?)";


        try {
            PreparedStatement pstmt = connectDB.prepareStatement(insertEmployee);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, contactNumber);
            pstmt.setString(4, password);
            pstmt.setBoolean(5, false);
            pstmt.setBoolean(6, true);

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
