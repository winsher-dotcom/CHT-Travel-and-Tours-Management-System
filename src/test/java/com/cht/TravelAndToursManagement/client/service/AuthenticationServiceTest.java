package com.cht.TravelAndToursManagement.client.service;

import com.cht.TravelAndToursManagement.client.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    EmployeeRepository mockRepo;

    @InjectMocks
    AuthenticationService service;

    @Test
    void authenticate_validCredentials_returnsTrue() {
        // Arrange
        when(mockRepo.validateCredentials("test@test.com", "password"))
                .thenReturn(true);

        // Act
        boolean result = service.authenticate("test@test.com", "password");

        // Assert
        assertTrue(result);
        verify(mockRepo).validateCredentials("test@test.com", "password");
    }
}
