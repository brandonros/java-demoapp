package com.example.demo.controller;

import com.example.demo.repository.HealthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthzControllerTest {

    @Mock
    private HealthRepository healthRepository;

    @InjectMocks
    private HealthzController healthzController;

    @Test
    void live_ReturnsOk() {
        ResponseEntity<String> response = healthzController.live();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OK", response.getBody());
    }

    @Test
    void ready_WhenBothDatabasesHealthy_ReturnsOk() {
        when(healthRepository.checkPrimaryDatabase()).thenReturn(true);

        ResponseEntity<String> response = healthzController.ready();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OK", response.getBody());
    }

    @Test
    void ready_WhenPrimaryDatabaseUnhealthy_ReturnsServiceUnavailable() {
        when(healthRepository.checkPrimaryDatabase()).thenReturn(false);

        ResponseEntity<String> response = healthzController.ready();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Database check failed - Primary: DOWN", response.getBody());
    }
}