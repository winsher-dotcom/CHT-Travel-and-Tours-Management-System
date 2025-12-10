package com.cht.TravelAndToursManagement.client.repository.impl;

import com.cht.TravelAndToursManagement.client.config.DatabaseConfig;
import com.cht.TravelAndToursManagement.client.model.Employee;
import com.cht.TravelAndToursManagement.client.repository.EmployeeRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.cht.TravelAndToursManagement.client.config.DatabaseConfig.dataSource;


public class EmployeeRepositoryImpl implements EmployeeRepository {

    public EmployeeRepositoryImpl(DataSource dataSource) {
        this.dataSource = DatabaseConfig.dataSource;
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        String sql = "SELECT * FROM employees WHERE email = ?";
        try (Connection connectDB = dataSource.getConnection(); PreparedStatement preparedStatement = connectDB.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapEmployee(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching employee by email", e);
        }
        return Optional.empty();
    }


    @Override
    public List<Employee> findAll() {
        return List.of();
    }

    @Override
    public Employee save(Employee employee) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public boolean validateCredentials(String email, String password) {
        String sql = "SELECT COUNT(1) FROM employees WHERE email = ? AND password = ?";
        try (Connection connectDB = dataSource.getConnection(); PreparedStatement preparedStatement = connectDB.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 1;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error validating employee credentials", e);
        }
        return false;
    }
}
