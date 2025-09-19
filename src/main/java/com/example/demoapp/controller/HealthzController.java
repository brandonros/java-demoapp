package com.example.demoapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demoapp.repository.HealthRepository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/healthz")
@Tag(name = "Health", description = "Kubernetes health check endpoints")
public class HealthzController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthzController.class);
    private final HealthRepository healthRepository;

    public HealthzController(HealthRepository healthRepository) {
        this.healthRepository = healthRepository;
    }

    @GetMapping("/live")
    @Operation(summary = "Liveness probe", description = "Indicates if the application is running")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is alive")
    })
    public ResponseEntity<String> live() {
        LOGGER.debug("Liveness probe called");
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/ready")
    @Operation(summary = "Readiness probe", description = "Indicates if the application is ready to serve requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is ready"),
        @ApiResponse(responseCode = "503", description = "Application is not ready")
    })
    public ResponseEntity<String> ready() {
        LOGGER.debug("Readiness probe called");

        // Run both database checks in parallel
        CompletableFuture<Boolean> primaryCheck = CompletableFuture.supplyAsync(() -> {
            boolean result = healthRepository.checkPrimaryDatabase();
            if (result) {
                LOGGER.debug("Primary database connection successful");
            } else {
                LOGGER.error("Primary database connection failed");
            }
            return result;
        });

        // Wait for both checks to complete (happens in parallel)
        CompletableFuture<Void> allChecks = CompletableFuture.allOf(primaryCheck);

        try {
            // Wait max 5 seconds for both checks
            allChecks.get(5, TimeUnit.SECONDS);

            boolean primaryHealthy = primaryCheck.get();

            if (primaryHealthy) {
                return ResponseEntity.ok("OK");
            } else {
                String message = String.format("Database check failed - Primary: %s",
                    primaryHealthy ? "UP" : "DOWN");
                return ResponseEntity.status(503).body(message);
            }
        } catch (TimeoutException e) {
            LOGGER.error("Health check timed out");
            return ResponseEntity.status(503).body("Health check timed out");
        } catch (Exception e) {
            LOGGER.error("Health check failed", e);
            return ResponseEntity.status(503).body("Health check failed");
        }
    }
}