package com.example.demo.controller;

import com.example.demo.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountController accountController;

    @Test
    void getAccount_WhenAccountExists_ReturnsAccount() {
        UUID uuid = UUID.randomUUID();
        Map<String, Object> account = new HashMap<>();
        account.put("id", uuid.toString());
        account.put("name", "Test Account");

        when(accountRepository.findByUuid(uuid)).thenReturn(Optional.of(account));

        ResponseEntity<?> response = accountController.getAccount(uuid);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(account, response.getBody());
    }

    @Test
    void getAccount_WhenAccountDoesNotExist_ReturnsNotFound() {
        UUID uuid = UUID.randomUUID();

        when(accountRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        ResponseEntity<?> response = accountController.getAccount(uuid);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}