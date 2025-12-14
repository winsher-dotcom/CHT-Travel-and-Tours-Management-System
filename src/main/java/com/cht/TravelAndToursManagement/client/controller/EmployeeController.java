package com.cht.TravelAndToursManagement.client.controller;

import com.cht.TravelAndToursManagement.client.model.Employee;
import com.cht.TravelAndToursManagement.client.navigation.NavigationService;
import com.cht.TravelAndToursManagement.client.navigation.Route;
import com.cht.TravelAndToursManagement.client.repository.EmployeeRepository;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class EmployeeController {

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

    private final EmployeeRepository employeeRepository;
    private final NavigationService navigationService;

    public EmployeeController(EmployeeRepository employeeRepository, NavigationService navigationService) {
        this.employeeRepository = employeeRepository;
        this.navigationService = navigationService;
    }


    // Table-building logic can be re-enabled and refactored to use employeeRepository
    // rather than direct JDBC when you're ready.
}
