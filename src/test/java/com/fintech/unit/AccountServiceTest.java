package com.fintech.unit;

import com.fintech.dto.request.AccountRequest;
import com.fintech.dto.response.AccountResponse;
import com.fintech.exception.AccountNotFoundException;
import com.fintech.exception.DuplicateAccountException;
import com.fintech.model.Account;
import com.fintech.model.Customer;
import com.fintech.model.User;
import com.fintech.repository.AccountRepository;
import com.fintech.repository.CustomerRepository;
import com.fintech.repository.UserRepository;
import com.fintech.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService - Pruebas Unitarias")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AccountService accountService;

    private User mockUser;
    private Customer mockCustomer;

    @BeforeEach
    void setUp() {
        mockUser = createMockUser("user-001", "john@example.com");
        mockCustomer = createMockCustomer("customer-001", mockUser, "John Doe");
    }

    private User createMockUser(String id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }

    private Customer createMockCustomer(String id, User user, String name) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setUser(user);
        customer.setName(name);
        return customer;
    }

    private Account createMockAccount(String id, String accountNumber, Customer customer, BigDecimal balance) {
        Account account = new Account();
        account.setId(id);
        account.setAccountNumber(accountNumber);
        account.setCustomer(customer);
        account.setBalance(balance);
        account.setActive(true);
        return account;
    }

    private void setupAuthentication(String email, User user, Customer customer) {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(customerRepository.findByUserId(user.getId())).thenReturn(Optional.of(customer));
    }

    @Test
    @DisplayName("Debe crear cuenta exitosamente")
    void createAccount_ValidData_Success() {
        // Arrange
        String accountNumber = "1234567890";
        BigDecimal initialBalance = new BigDecimal("1000.00");
        AccountRequest request = new AccountRequest(accountNumber, initialBalance);

        setupAuthentication("john@example.com", mockUser, mockCustomer);
        when(accountRepository.existsByAccountNumber(accountNumber)).thenReturn(false);

        Account savedAccount = createMockAccount("acc-001", accountNumber, mockCustomer, initialBalance);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // Act
        AccountResponse response = accountService.createAccount(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("acc-001");
        assertThat(response.accountNumber()).isEqualTo(accountNumber);
        assertThat(response.customerName()).isEqualTo("John Doe");
        assertThat(response.balance()).isEqualByComparingTo(initialBalance);
        assertThat(response.active()).isTrue();

        verify(accountRepository).existsByAccountNumber(accountNumber);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el número de cuenta ya existe")
    void createAccount_DuplicateNumber_ThrowsException() {
        // Arrange
        AccountRequest request = new AccountRequest("1234567890", new BigDecimal("1000.00"));
        when(accountRepository.existsByAccountNumber("1234567890")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(DuplicateAccountException.class)
                .hasMessageContaining("already exists");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Debe obtener cuenta por ID exitosamente")
    void getAccountById_ExistingId_Success() {
        // Arrange
        setupAuthentication("john@example.com", mockUser, mockCustomer);
        Account account = createMockAccount("acc-001", "1234567890", mockCustomer, new BigDecimal("1000.00"));
        when(accountRepository.findById("acc-001")).thenReturn(Optional.of(account));

        // Act
        AccountResponse response = accountService.getAccountById("acc-001");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("acc-001");
        assertThat(response.customerName()).isEqualTo("John Doe");
        verify(accountRepository).findById("acc-001");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el ID no existe")
    void getAccountById_NonExistingId_ThrowsException() {
        // Arrange
        when(accountRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accountService.getAccountById("invalid-id"))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Debe listar todas las cuentas del usuario autenticado")
    void getAllAccounts_MultipleAccounts_Success() {
        // Arrange
        Customer otherCustomer = createMockCustomer("customer-002", createMockUser("user-002", "jane@example.com"), "Jane Smith");

        setupAuthentication("john@example.com", mockUser, mockCustomer);

        Account account1 = createMockAccount("acc-001", "1234567890", mockCustomer, new BigDecimal("1000.00"));
        Account account2 = createMockAccount("acc-002", "0987654321", otherCustomer, new BigDecimal("2000.00"));

        when(accountRepository.findAll()).thenReturn(Arrays.asList(account1, account2));

        // Act
        List<AccountResponse> responses = accountService.getAllAccounts();

        // Assert
        assertThat(responses).hasSize(1); // Solo la cuenta del usuario autenticado
        assertThat(responses.get(0).accountNumber()).isEqualTo("1234567890");
    }

    @Test
    @DisplayName("Debe listar solo las cuentas activas del usuario")
    void getActiveAccounts_FilterActive_Success() {
        // Arrange
        setupAuthentication("john@example.com", mockUser, mockCustomer);
        Account activeAccount = createMockAccount("acc-001", "1234567890", mockCustomer, new BigDecimal("1000.00"));
        when(accountRepository.findByActive(true)).thenReturn(Arrays.asList(activeAccount));

        // Act
        List<AccountResponse> responses = accountService.getActiveAccounts();

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).active()).isTrue();
    }

    @Test
    @DisplayName("Debe desactivar cuenta exitosamente")
    void deactivateAccount_ActiveAccount_Success() {
        // Arrange
        setupAuthentication("john@example.com", mockUser, mockCustomer);
        Account account = createMockAccount("acc-001", "1234567890", mockCustomer, new BigDecimal("1000.00"));
        when(accountRepository.findById("acc-001")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        AccountResponse response = accountService.deactivateAccount("acc-001");

        // Assert
        assertThat(response.id()).isEqualTo("acc-001");
        assertThat(response.active()).isFalse();
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Debe activar cuenta exitosamente")
    void activateAccount_InactiveAccount_Success() {
        // Arrange
        setupAuthentication("john@example.com", mockUser, mockCustomer);
        Account account = createMockAccount("acc-001", "1234567890", mockCustomer, new BigDecimal("1000.00"));
        account.setActive(false);
        when(accountRepository.findById("acc-001")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        AccountResponse response = accountService.activateAccount("acc-001");

        // Assert
        assertThat(response.active()).isTrue();
    }

    @Test
    @DisplayName("Debe retornar el saldo de la cuenta")
    void getAccountBalance_ExistingAccount_Success() {
        // Arrange
        setupAuthentication("john@example.com", mockUser, mockCustomer);
        Account account = createMockAccount("acc-001", "1234567890", mockCustomer, new BigDecimal("1000.00"));
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(account));

        // Act
        BigDecimal balance = accountService.getAccountBalance("1234567890");

        // Assert
        assertThat(balance).isEqualByComparingTo(new BigDecimal("1000.00"));
    }
}
