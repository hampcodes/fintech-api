package com.fintech.unit;

import com.fintech.api.dto.request.AccountRequest;
import com.fintech.api.dto.response.AccountResponse;
import com.fintech.api.exception.AccountNotFoundException;
import com.fintech.api.exception.DuplicateAccountException;
import com.fintech.api.model.Account;
import com.fintech.api.repository.AccountRepository;
import com.fintech.api.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService - Pruebas Unitarias")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private AccountRequest testRequest;

    @BeforeEach
    void setUp() {
        testAccount = new Account("1234567890", "John Doe",
                "john@example.com", new BigDecimal("1000.00"));
        testAccount.setId("acc-001");

        testRequest = new AccountRequest(
                "1234567890",
                "John Doe",
                "john@example.com",
                new BigDecimal("1000.00")
        );
    }

    @Test
    @DisplayName("Debe crear cuenta exitosamente cuando el número no existe")
    void createAccount_ValidData_Success() {
        // Given
        given(accountRepository.existsByAccountNumber("1234567890")).willReturn(false);
        given(accountRepository.save(any(Account.class))).willReturn(testAccount);

        // When
        AccountResponse response = accountService.createAccount(testRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.accountNumber()).isEqualTo("1234567890");
        assertThat(response.ownerName()).isEqualTo("John Doe");
        assertThat(response.balance()).isEqualByComparingTo(new BigDecimal("1000.00"));

        then(accountRepository).should(times(1)).existsByAccountNumber("1234567890");
        then(accountRepository).should(times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el número de cuenta ya existe")
    void createAccount_DuplicateNumber_ThrowsException() {
        // Given
        given(accountRepository.existsByAccountNumber("1234567890")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> accountService.createAccount(testRequest))
                .isInstanceOf(DuplicateAccountException.class)
                .hasMessageContaining("already exists");

        then(accountRepository).should(times(1)).existsByAccountNumber("1234567890");
        then(accountRepository).should(times(0)).save(any(Account.class));
    }

    @Test
    @DisplayName("Debe obtener cuenta por ID exitosamente")
    void getAccountById_ExistingId_Success() {
        // Given
        given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));

        // When
        AccountResponse response = accountService.getAccountById("acc-001");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("acc-001");
        assertThat(response.accountNumber()).isEqualTo("1234567890");

        then(accountRepository).should(times(1)).findById("acc-001");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el ID no existe")
    void getAccountById_NonExistingId_ThrowsException() {
        // Given
        given(accountRepository.findById("invalid-id")).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.getAccountById("invalid-id"))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("not found");

        then(accountRepository).should(times(1)).findById("invalid-id");
    }

    @Test
    @DisplayName("Debe obtener cuenta por número exitosamente")
    void getAccountByNumber_ExistingNumber_Success() {
        // Given
        given(accountRepository.findByAccountNumber("1234567890"))
                .willReturn(Optional.of(testAccount));

        // When
        AccountResponse response = accountService.getAccountByNumber("1234567890");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.accountNumber()).isEqualTo("1234567890");

        then(accountRepository).should(times(1)).findByAccountNumber("1234567890");
    }

    @Test
    @DisplayName("Debe listar todas las cuentas")
    void getAllAccounts_MultipleAccounts_Success() {
        // Given
        Account account2 = new Account("0987654321", "Jane Smith",
                "jane@example.com", new BigDecimal("2000.00"));
        account2.setId("acc-002");

        List<Account> accounts = Arrays.asList(testAccount, account2);
        given(accountRepository.findAll()).willReturn(accounts);

        // When
        List<AccountResponse> responses = accountService.getAllAccounts();

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).accountNumber()).isEqualTo("1234567890");
        assertThat(responses.get(1).accountNumber()).isEqualTo("0987654321");

        then(accountRepository).should(times(1)).findAll();
    }

    @Test
    @DisplayName("Debe listar solo las cuentas activas")
    void getActiveAccounts_FilterActive_Success() {
        // Given
        List<Account> activeAccounts = Arrays.asList(testAccount);
        given(accountRepository.findByActive(true)).willReturn(activeAccounts);

        // When
        List<AccountResponse> responses = accountService.getActiveAccounts();

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(1);

        then(accountRepository).should(times(1)).findByActive(true);
    }

    @Test
    @DisplayName("Debe desactivar cuenta exitosamente")
    void deactivateAccount_ActiveAccount_Success() {
        // Given
        given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));
        given(accountRepository.save(any(Account.class))).willReturn(testAccount);

        // When
        AccountResponse response = accountService.deactivateAccount("acc-001");

        // Then
        assertThat(response).isNotNull();
        assertThat(testAccount.getActive()).isFalse();

        then(accountRepository).should(times(1)).findById("acc-001");
        then(accountRepository).should(times(1)).save(testAccount);
    }

    @Test
    @DisplayName("Debe activar cuenta exitosamente")
    void activateAccount_InactiveAccount_Success() {
        // Given
        testAccount.deactivate();
        given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));
        given(accountRepository.save(any(Account.class))).willReturn(testAccount);

        // When
        AccountResponse response = accountService.activateAccount("acc-001");

        // Then
        assertThat(response).isNotNull();
        assertThat(testAccount.getActive()).isTrue();

        then(accountRepository).should(times(1)).findById("acc-001");
        then(accountRepository).should(times(1)).save(testAccount);
    }

    @Test
    @DisplayName("Debe retornar el saldo de la cuenta")
    void getAccountBalance_ExistingAccount_Success() {
        // Given
        given(accountRepository.findByAccountNumber("1234567890"))
                .willReturn(Optional.of(testAccount));

        // When
        BigDecimal balance = accountService.getAccountBalance("1234567890");

        // Then
        assertThat(balance).isEqualByComparingTo(new BigDecimal("1000.00"));

        then(accountRepository).should(times(1)).findByAccountNumber("1234567890");
    }
}
