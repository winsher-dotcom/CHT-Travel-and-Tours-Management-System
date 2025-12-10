package com.cht.TravelAndToursManagement.client.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static java.util.Locale.lookup;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/login.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void loginButtonDisabledWhenFieldsEmpty() {

        Button loginBtn = lookup("#loginButton").query();

        assertTrue(loginBtn.isDisabled());
    }
}
