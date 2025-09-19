package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Utility", description = "Utility and diagnostic endpoints")
public class PingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingController.class);

    @GetMapping("/ping")
    @Operation(summary = "Ping endpoint", description = "Returns pong to verify the service is running")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response")
    })
    public ResponseEntity<String> ping() {
        LOGGER.info("Ping endpoint called");
        return ResponseEntity.ok("pong");
    }
}