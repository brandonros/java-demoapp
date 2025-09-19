package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Health check endpoints")
public class PingController {

    private static final Logger logger = LoggerFactory.getLogger(PingController.class);

    @GetMapping("/ping")
    @Operation(summary = "Ping endpoint", description = "Returns pong to verify the service is running")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response")
    })
    public String ping() {
        logger.info("Ping endpoint called");
        return "pong";
    }
}