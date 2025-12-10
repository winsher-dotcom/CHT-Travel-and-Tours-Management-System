package com.cht.TravelAndToursManagement.client.controller;

import com.cht.TravelAndToursManagement.client.config.DatabaseConfig;
import com.cht.TravelAndToursManagement.client.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmployeeController extends SceneController {

    @FXML
    private TableView<Employee> TableContainer;
    @FXML
    private TableColumn<Employee, String> colName;
    @FXML
    private TableColumn<Employee, String> colEmail;
    @FXML
    private TableColumn<Employee, String> colContact;
    @FXML
    private TableColumn<Employee, String> colManager;
    @FXML
    private TableColumn<Employee, String> colActive;

    @FXML
    public void goToEmployee() {
        setCenter("/com/cht/TravelAndToursManagement/view/Employee-view.fxml");
    }

    @FXML
    public void addBooking() throws IOException {
        setCenter("/com/cht/TravelAndToursManagement/view/AddBooking1-view.fxml");

    }

    public void initialize() {
        buildTable();
    }

    public void buildTable() {
        String employeeViewQuery = "SELECT name, email, contactNumber, isManager, isActive FROM employee";

        ObservableList<Employee> data = FXCollections.observableArrayList();

        try (Connection connectDB = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(employeeViewQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                data.add(new Employee(
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("contactNumber"),
                        resultSet.getBoolean("isManager"),
                        resultSet.getBoolean("isActive")
                ));
            }
            colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
            colContact.setCellValueFactory(cellData -> cellData.getValue().contactNumberProperty());
            colManager.setCellValueFactory(cellData -> cellData.getValue().isManager());
            colActive.setCellValueFactory(cellData -> cellData.getValue().isActive());
            TableContainer.setItems(data);


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }


}
