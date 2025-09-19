package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/healthz")
@Tag(name = "Health", description = "Kubernetes health check endpoints")
public class HealthzController {

    private static final Logger logger = LoggerFactory.getLogger(HealthzController.class);
    private final JdbcTemplate primaryJdbcTemplate;
    private final JdbcTemplate secondaryJdbcTemplate;

    public HealthzController(
            @Qualifier("primaryJdbcTemplate") JdbcTemplate primaryJdbcTemplate,
            @Qualifier("secondaryJdbcTemplate") JdbcTemplate secondaryJdbcTemplate) {
        this.primaryJdbcTemplate = primaryJdbcTemplate;
        this.secondaryJdbcTemplate = secondaryJdbcTemplate;
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

        // Run both database checks in parallel
        CompletableFuture<Boolean> primaryCheck = CompletableFuture.supplyAsync(() -> {
            try {
                primaryJdbcTemplate.queryForObject("SELECT 1", Integer.class);
                logger.debug("Primary database connection successful");
                return true;
            } catch (Exception e) {
                logger.error("Primary database connection failed: {}", e.getMessage());
                return false;
            }
        });

        CompletableFuture<Boolean> secondaryCheck = CompletableFuture.supplyAsync(() -> {
            try {
                secondaryJdbcTemplate.queryForObject("SELECT 1", Integer.class);
                logger.debug("Secondary database connection successful");
                return true;
            } catch (Exception e) {
                logger.error("Secondary database connection failed: {}", e.getMessage());
                return false;
            }
        });

        // Wait for both checks to complete (happens in parallel)
        CompletableFuture<Void> allChecks = CompletableFuture.allOf(primaryCheck, secondaryCheck);

        try {
            // Wait max 5 seconds for both checks
            allChecks.get(5, TimeUnit.SECONDS);

            boolean primaryHealthy = primaryCheck.get();
            boolean secondaryHealthy = secondaryCheck.get();

            if (primaryHealthy && secondaryHealthy) {
                return ResponseEntity.ok("OK");
            } else {
                String message = String.format("Database check failed - Primary: %s, Secondary: %s",
                    primaryHealthy ? "UP" : "DOWN",
                    secondaryHealthy ? "UP" : "DOWN");
                return ResponseEntity.status(503).body(message);
            }
        } catch (TimeoutException e) {
            logger.error("Health check timed out");
            return ResponseEntity.status(503).body("Health check timed out");
        } catch (Exception e) {
            logger.error("Health check failed", e);
            return ResponseEntity.status(503).body("Health check failed");
        }
    }
}