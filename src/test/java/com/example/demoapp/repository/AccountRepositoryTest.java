package com.example.demoapp.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demoapp.repository.AccountRepository;

import org.springframework.dao.EmptyResultDataAccessException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AccountRepository accountRepository;

    @Test
    void findByUuid_WhenAccountExists_ReturnsAccount() {
        UUID uuid = UUID.randomUUID();
        Map<String, Object> expectedAccount = new HashMap<>();
        expectedAccount.put("id", uuid.toString());
        expectedAccount.put("name", "Test Account");

        when(jdbcTemplate.queryForMap("EXEC sp_GetAccountByUuid ?", uuid.toString()))
                .thenReturn(expectedAccount);

        Optional<Map<String, Object>> result = accountRepository.findByUuid(uuid);

        assertTrue(result.isPresent());
        assertEquals(expectedAccount, result.get());
    }

    @Test
    void findByUuid_WhenAccountDoesNotExist_ReturnsEmpty() {
        UUID uuid = UUID.randomUUID();

        when(jdbcTemplate.queryForMap("EXEC sp_GetAccountByUuid ?", uuid.toString()))
                .thenThrow(new EmptyResultDataAccessException(1));

        Optional<Map<String, Object>> result = accountRepository.findByUuid(uuid);

        assertFalse(result.isPresent());
    }
}