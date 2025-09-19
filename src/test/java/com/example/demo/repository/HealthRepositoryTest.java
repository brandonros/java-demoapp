package com.example.demo.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demoapp.repository.HealthRepository;

import org.springframework.dao.DataAccessException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthRepositoryTest {

    @Mock
    private JdbcTemplate primaryJdbcTemplate;

    private HealthRepository healthRepository;

    @BeforeEach
    void setUp() {
        healthRepository = new HealthRepository(primaryJdbcTemplate);
    }

    @Test
    void checkPrimaryDatabase_WhenDatabaseIsHealthy_ReturnsTrue() {
        when(primaryJdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .thenReturn(1);

        boolean result = healthRepository.checkPrimaryDatabase();

        assertTrue(result);
    }

    @Test
    void checkPrimaryDatabase_WhenDatabaseIsUnhealthy_ReturnsFalse() {
        when(primaryJdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .thenThrow(new DataAccessException("Connection failed") {});

        boolean result = healthRepository.checkPrimaryDatabase();

        assertFalse(result);
    }
}