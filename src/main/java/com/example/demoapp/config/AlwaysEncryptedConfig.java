package com.example.demoapp.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.ManagedIdentityCredential;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionAzureKeyVaultProvider;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import jakarta.annotation.PostConstruct;

@Configuration
@ConditionalOnProperty(
    name = "sqlserver.always-encrypted.azure-key-vault.enabled", 
    havingValue = "true", 
    matchIfMissing = false
)
public class AlwaysEncryptedConfig {

    @PostConstruct
    public void configureAlwaysEncrypted() {
        try {
            SQLServerColumnEncryptionAzureKeyVaultProvider akvProvider;
            ManagedIdentityCredential credential = new ManagedIdentityCredentialBuilder()
                .build();
            akvProvider = new SQLServerColumnEncryptionAzureKeyVaultProvider(credential);
            Map<String, SQLServerColumnEncryptionKeyStoreProvider> keyStoreMap = new HashMap<>();
            keyStoreMap.put(akvProvider.getName(), akvProvider);
            SQLServerConnection.registerColumnEncryptionKeyStoreProviders(keyStoreMap);
            
            System.out.println("Azure Key Vault provider for Always Encrypted has been registered");
        } catch (SQLServerException e) {
            throw new RuntimeException("Failed to register Azure Key Vault provider", e);
        }
    }
}