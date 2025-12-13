package com.cht.TravelAndToursManagement.client.controller.booking;

import com.cht.TravelAndToursManagement.client.controller.ErrorDialog;
import com.cht.TravelAndToursManagement.client.navigation.FXMLPaths;
import com.cht.TravelAndToursManagement.client.navigation.NavigationService;
import com.cht.TravelAndToursManagement.client.navigation.Route;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

import static com.cht.TravelAndToursManagement.client.controller.MainLayoutController.logger;

public class BookingController {

    // Reusable error dialog instance
    private final ErrorDialog Error = new ErrorDialog();

    private final NavigationService navigationService;

    public BookingController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

//    /**
//     * Swap the center content of the main layout while keeping the sidebar intact.
//     */
//    private void setCenterContent(String fxmlPath) {
//        try {
//            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
//            Node node = loader.load();
//            if (contentArea != null) {
//                contentArea.setCenter(node);
//            }
//        } catch (IOException e) {
//            logger.error("Failed to load center content FXML: {}", fxmlPath, e);
//            Error.showError("Failed to load content");
//        }
//    }

    @FXML
    public void addBooking() {
        // Load first booking step inside the existing main layout (sidebar preserved)
        navigationService.navigateTo(Route.BOOKING_STEP1);
    }
}
