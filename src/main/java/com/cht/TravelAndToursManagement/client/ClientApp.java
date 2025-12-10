package com.cht.TravelAndToursManagement.client;

import com.cht.TravelAndToursManagement.client.config.DatabaseConfig;
import com.cht.TravelAndToursManagement.client.controller.AuthController;
import com.cht.TravelAndToursManagement.client.controller.EmployeeController;
import com.cht.TravelAndToursManagement.client.controller.MainLayoutController;
import com.cht.TravelAndToursManagement.client.navigation.ControllerFactory;
import com.cht.TravelAndToursManagement.client.navigation.NavigationService;
import com.cht.TravelAndToursManagement.client.navigation.Route;
import com.cht.TravelAndToursManagement.client.repository.EmployeeRepository;
import com.cht.TravelAndToursManagement.client.repository.impl.EmployeeRepositoryImpl;
import com.cht.TravelAndToursManagement.client.service.AuthenticationService;
import com.cht.TravelAndToursManagement.client.service.DashboardService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.io.IOException;


public class ClientApp extends Application {


    @Override
    public void start(Stage primaryStage) throws IOException {
        // Create infrastructure
        DataSource dataSource = DatabaseConfig.getDataSource();

        // Create repositories
        EmployeeRepository employeeRepository = new EmployeeRepositoryImpl(dataSource);
        BookingRepository bookingRepository = new BookingRepositoryImpl(dataSource);
        CustomerRepository customerRepository = new CustomerRepositoryImpl(dataSource);

        // Create services
        AuthenticationService authService = new AuthenticationService(employeeRepository);
        DashboardService dashboardService = new DashboardService(bookingRepository, customerRepository);

        // Create controller factory
        controllerFactory = new ControllerFactory();
        navigationService = new NavigationService(primaryStage, controllerFactory);

        // Register controllers with dependencies
        controllerFactory.registerController(
                AuthController.class,
                new AuthController(authService, navigationService)
        );
        controllerFactory.registerController(
                MainLayoutController.class,
                new MainLayoutController(dashboardService, navigationService)
        );
        controllerFactory.registerController(
                EmployeeController.class,
                new EmployeeController(employeeRepository, navigationService)
        );

        // Start navigation (shows login screen)
        navigationService.navigateTo(Route.LOGIN);

        primaryStage.setTitle("CHT Travel & Tours");

    }

    private void showErrorDialog(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }


}