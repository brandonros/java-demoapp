package com.example.demo.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRepository.class);
    private final JdbcTemplate jdbc;

    public AccountRepository(@Qualifier("primaryJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Map<String, Object>> findByUuid(UUID uuid) {
        try {
            LOGGER.debug("Executing stored procedure sp_GetAccountByUuid for UUID: {}", uuid);

            // Call stored procedure - using EXEC for MS SQL
            Map<String, Object> result = jdbc.queryForMap(
                "EXEC sp_GetAccountByUuid ?",
                uuid.toString()
            );

            return Optional.of(result);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug("No account found for UUID: {}", uuid);
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.error("Error fetching account for UUID: {}", uuid, e);
            throw e;
        }
    }
}