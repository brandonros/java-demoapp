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
        
        try {
            if (healthRepository.checkPrimaryDatabase()) {
                LOGGER.debug("Primary database connection successful");
                return ResponseEntity.ok("OK");
            } else {
                LOGGER.error("Primary database connection failed");
                return ResponseEntity.status(503).body("Database check failed");
            }
        } catch (Exception e) {
            LOGGER.error("Health check failed", e);
            return ResponseEntity.status(503).body("Health check failed: " + e.getMessage());
        }
    }
}