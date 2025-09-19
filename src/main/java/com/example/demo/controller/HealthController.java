package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthz")
@Tag(name = "Health", description = "Kubernetes health check endpoints")
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/live")
    @Operation(summary = "Liveness probe", description = "Indicates if the application is running")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is alive")
    })
    public ResponseEntity<String> live() {
        logger.debug("Liveness probe called");
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/ready")
    @Operation(summary = "Readiness probe", description = "Indicates if the application is ready to serve requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is ready"),
        @ApiResponse(responseCode = "503", description = "Application is not ready")
    })
    public ResponseEntity<String> ready() {
        logger.debug("Readiness probe called");

        try {
            // Simple database connectivity check
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            logger.debug("Database connection successful");
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            logger.error("Database connection failed: {}", e.getMessage());
            return ResponseEntity.status(503).body("Database connection failed");
        }
    }
}