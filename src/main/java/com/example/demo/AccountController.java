package com.example.demo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
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

@org.springframework.stereotype.Repository
class AccountRepository {

    private static final Logger logger = LoggerFactory.getLogger(AccountRepository.class);
    private final JdbcTemplate jdbc;

    AccountRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    Optional<Map<String, Object>> findByUuid(UUID uuid) {
        try {
            logger.debug("Executing stored procedure sp_GetAccountByUuid for UUID: {}", uuid);

            // Call stored procedure - using EXEC for MS SQL
            Map<String, Object> result = jdbc.queryForMap(
                "EXEC sp_GetAccountByUuid ?",
                uuid.toString()
            );

            return Optional.of(result);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No account found for UUID: {}", uuid);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error fetching account for UUID: {}", uuid, e);
            throw e;
        }
    }
}