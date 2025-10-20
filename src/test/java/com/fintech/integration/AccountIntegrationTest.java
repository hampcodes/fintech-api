package com.fintech.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.api.dto.request.AccountRequest;
import com.fintech.api.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Account - Pruebas de Integración")
class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Debe crear cuenta exitosamente con datos válidos")
    void createAccount_ValidData_ReturnsCreated() throws Exception {
        // Given
        AccountRequest request = new AccountRequest(
                "1234567890",
                "John Doe",
                "john@example.com",
                new BigDecimal("1000.00")
        );

        // When & Then
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.accountNumber", is("1234567890")))
                .andExpect(jsonPath("$.ownerName", is("John Doe")))
                .andExpect(jsonPath("$.ownerEmail", is("john@example.com")))
                .andExpect(jsonPath("$.balance", is(1000.00)))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    @DisplayName("Debe rechazar número de cuenta duplicado")
    void createAccount_DuplicateNumber_ReturnsConflict() throws Exception {
        // Given - Create first account
        AccountRequest request = new AccountRequest(
                "1234567890",
                "John Doe",
                "john@example.com",
                new BigDecimal("1000.00")
        );

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // When & Then - Try to create duplicate
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("Debe rechazar cuenta con email inválido")
    void createAccount_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Given
        String requestJson = """
                {
                    "accountNumber": "1234567890",
                    "ownerName": "John Doe",
                    "ownerEmail": "invalid-email",
                    "initialBalance": 1000.00
                }
                """;

        // When & Then
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe rechazar cuenta con saldo negativo")
    void createAccount_NegativeBalance_ReturnsBadRequest() throws Exception {
        // Given
        AccountRequest request = new AccountRequest(
                "1234567890",
                "John Doe",
                "john@example.com",
                new BigDecimal("-100.00")
        );

        // When & Then
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe obtener cuenta por ID exitosamente")
    void getAccountById_ExistingId_ReturnsOk() throws Exception {
        // Given - Create account
        AccountRequest request = new AccountRequest(
                "1234567890",
                "John Doe",
                "john@example.com",
                new BigDecimal("1000.00")
        );

        String createResponse = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accountId = objectMapper.readTree(createResponse).get("id").asText();

        // When & Then
        mockMvc.perform(get("/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(accountId)))
                .andExpect(jsonPath("$.accountNumber", is("1234567890")));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el ID no existe")
    void getAccountById_NonExistingId_ReturnsNotFound() throws Exception {
        // Given
        String fakeId = "non-existent-id";

        // When & Then
        mockMvc.perform(get("/accounts/" + fakeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    @DisplayName("Debe obtener cuenta por número exitosamente")
    void getAccountByNumber_ExistingNumber_ReturnsOk() throws Exception {
        // Given - Create account
        AccountRequest request = new AccountRequest(
                "1234567890",
                "John Doe",
                "john@example.com",
                new BigDecimal("1000.00")
        );

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // When & Then
        mockMvc.perform(get("/accounts/number/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", is("1234567890")))
                .andExpect(jsonPath("$.ownerName", is("John Doe")));
    }

    @Test
    @DisplayName("Debe listar todas las cuentas")
    void getAllAccounts_MultipleAccounts_ReturnsOk() throws Exception {
        // Given - Create 3 accounts
        for (int i = 1; i <= 3; i++) {
            AccountRequest request = new AccountRequest(
                    "123456789" + i,
                    "User " + i,
                    "user" + i + "@example.com",
                    new BigDecimal("1000.00")
            );

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        // When & Then
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("Debe desactivar cuenta exitosamente")
    void deactivateAccount_ActiveAccount_ReturnsOk() throws Exception {
        // Given - Create account
        AccountRequest request = new AccountRequest(
                "1234567890",
                "John Doe",
                "john@example.com",
                new BigDecimal("1000.00")
        );

        String createResponse = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accountId = objectMapper.readTree(createResponse).get("id").asText();

        // When & Then
        mockMvc.perform(patch("/accounts/" + accountId + "/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(false)));
    }

    @Test
    @DisplayName("Debe activar cuenta exitosamente")
    void activateAccount_InactiveAccount_ReturnsOk() throws Exception {
        // Given - Create and deactivate account
        AccountRequest request = new AccountRequest(
                "1234567890",
                "John Doe",
                "john@example.com",
                new BigDecimal("1000.00")
        );

        String createResponse = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accountId = objectMapper.readTree(createResponse).get("id").asText();

        mockMvc.perform(patch("/accounts/" + accountId + "/deactivate"))
                .andExpect(status().isOk());

        // When & Then
        mockMvc.perform(patch("/accounts/" + accountId + "/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    @DisplayName("Debe obtener saldo de cuenta exitosamente")
    void getAccountBalance_ExistingAccount_ReturnsOk() throws Exception {
        // Given - Create account
        AccountRequest request = new AccountRequest(
                "1234567890",
                "John Doe",
                "john@example.com",
                new BigDecimal("1500.50")
        );

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // When & Then
        mockMvc.perform(get("/accounts/number/1234567890/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("1500.50"));
    }
}
