package com.cht.TravelAndToursManagement.client.controller;


import com.cht.TravelAndToursManagement.client.config.DatabaseConfig;
import com.cht.TravelAndToursManagement.client.navigation.NavigationService;
import com.cht.TravelAndToursManagement.client.navigation.Route;
import com.cht.TravelAndToursManagement.client.service.AuthenticationService;
import javafx.concurrent.Task;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.sql.*;

public class AuthController extends SceneController {
    private final AuthenticationService authService;
    private final NavigationService navigationService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


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

    public AuthController(AuthenticationService authService, NavigationService navigationService) {
        this.authService = authService;
        this.navigationService = navigationService;
    }


    @FXML
    public void onLoginButtonClicked() {
        String email = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        if (email.isBlank() || password.isBlank()) {
            loginMessageLabel.setText("Username or password is empty");
            return;
        }
        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() {
                return authService.authenticate(email, password);
            }
        };

        loginTask.setOnSucceeded(event -> {
            if (loginTask.getValue()) {
                NavigationService.navigateTo(Route.DASHBOARD);
            } else {
                loginMessageLabel.setText("Invalid credentials");
            }
        });

        new Thread(loginTask).start();
    }

    public void cancelButton() throws IOException {
        Stage stage = (Stage) loginContainer.getScene().getWindow();
        stage.close();

    }

    public void validateLogin() {
        String email = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        String verifyLogin = "SELECT COUNT(1) FROM Employee WHERE Email = ? AND Password = ?";

        try (Connection connectDB = DatabaseConfig.getConnection();
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
                        logger.error(e.getMessage());
                    }
//                    loginMessageLabel.setText("Login Successfully!");
                } else {
                    loginMessageLabel.setText("Invalid Login. Please try again.");
                }
            }
        } catch (SQLException e) {
            logger.error("Database error during login", e);
        }
    }

    public void registerEmployee() {
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String contactNumber = contactNumberTextField.getText();
        String password = confirmPasswordField.getText();

        String insertEmployee = "INSERT INTO Employee (name, email, contactNumber, password, isManager, isActive) VALUES (?, ?, ?, ?, ?, ?)";


        try (Connection connectDB = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(insertEmployee)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, contactNumber);
            preparedStatement.setString(4, password);
            preparedStatement.setBoolean(5, false);
            preparedStatement.setBoolean(6, true);

            registerMessageLabel.setText("Account Created Successfully!");
        } catch (Exception e) {
            logger.error(e.getMessage());
            registerMessageLabel.setText("Error Creating Account!");
        }

    }

    public void createAccountButton(ActionEvent event) {
        setCenter("/com/cht/TravelAndToursManagement/view/Register-view.fxml");
    }


}
