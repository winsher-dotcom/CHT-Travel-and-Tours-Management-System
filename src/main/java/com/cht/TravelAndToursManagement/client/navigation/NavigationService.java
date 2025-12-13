package com.cht.TravelAndToursManagement.client.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;


public class NavigationService {
    private final Stage primaryStage;
    private final Map<Route, String> routeMap;
    private final ControllerFactory controllerFactory;

    public NavigationService(Stage primaryStage, ControllerFactory factory) {
        this.primaryStage = Objects.requireNonNull(primaryStage, "primaryStage must not be null");
        this.controllerFactory = Objects.requireNonNull(factory, "controllerFactory must not be null");
        this.routeMap = initializedRoutes();
    }

    private Map<Route, String> initializedRoutes() {
        Map<Route, String> map = new EnumMap<>(Route.class);
        map.put(Route.LOGIN, FXMLPaths.LOGIN);
        map.put(Route.DASHBOARD, FXMLPaths.DASHBOARD);
        map.put(Route.EMPLOYEE, FXMLPaths.EMPLOYEE);
        map.put(Route.REGISTER, FXMLPaths.REGISTER);
        map.put(Route.BOOKING, FXMLPaths.BOOKING);
        map.put(Route.BOOKING_STEP1, FXMLPaths.BOOKING_STEP1);
        return map;
    }

    public void navigateTo(Route route) {
        String fxmlPath = routeMap.get(route);
        if (fxmlPath == null) {
            throw new NavigationException("No FXML mapping found for route: " + route, null);
        }

        try {
            FXMLLoader loader = new FXMLLoader(NavigationService.class.getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            

            primaryStage.setMaximized(true);

            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new NavigationException("Failed to navigate to " + route + " using FXML: " + fxmlPath, e);
        }
    }
}
