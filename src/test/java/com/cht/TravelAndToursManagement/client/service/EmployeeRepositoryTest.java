package com.cht.TravelAndToursManagement.client.service;

import com.cht.TravelAndToursManagement.client.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeeRepositoryTest {
    private EmployeeRepository repository;

    @BeforeAll
    void setup() {
        repository = new EmployeeRepository(TestDatabaseConfig::getConnection);
    }

    @Test
    void validateCredentials_returnsTrueForValidUser() {

        repository.save("test@test.com", "12345");

        boolean result = repository.validateCredentials("test@test.com", "12345");

        assertTrue(result);
    }
}
