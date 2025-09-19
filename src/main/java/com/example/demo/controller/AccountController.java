package com.example.demo.controller;

import com.example.demo.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Account management endpoints")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountRepository repository;

    public AccountController(AccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get account by UUID", description = "Retrieves account details from database using UUID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account found"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<?> getAccount(
            @Parameter(description = "Account UUID", required = true)
            @PathVariable UUID uuid) {

        logger.info("Fetching account with UUID: {}", uuid);

        return repository.findByUuid(uuid)
            .map(account -> {
                logger.info("Account found: {}", uuid);
                return ResponseEntity.ok(account);
            })
            .orElseGet(() -> {
                logger.warn("Account not found: {}", uuid);
                return ResponseEntity.notFound().build();
            });
    }
}