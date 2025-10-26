package com.fintech.unit;

import com.fintech.dto.request.TransactionRequest;
import com.fintech.dto.response.TransactionResponse;
import com.fintech.exception.AccountNotFoundException;
import com.fintech.exception.InactiveAccountException;
import com.fintech.exception.InsufficientBalanceException;
import com.fintech.exception.TransactionNotFoundException;
import com.fintech.model.Account;
import com.fintech.model.Customer;
import com.fintech.model.Transaction;
import com.fintech.model.TransactionType;
import com.fintech.model.User;
import com.fintech.repository.AccountRepository;
import com.fintech.repository.TransactionRepository;
import com.fintech.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService - Pruebas Unitarias")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

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

    private Account createMockAccount(String id, String accountNumber, Customer customer, BigDecimal balance, Boolean active) {
        Account account = new Account();
        account.setId(id);
        account.setAccountNumber(accountNumber);
        account.setCustomer(customer);
        account.setBalance(balance);
        account.setActive(active);
        return account;
    }

    @Test
    @DisplayName("Debe crear un depósito exitosamente")
    void createTransaction_Deposit_Success() {
        // Arrange
        String accountNumber = "1234567890";
        BigDecimal currentBalance = new BigDecimal("1000.00");
        BigDecimal depositAmount = new BigDecimal("500.00");
        TransactionRequest request = new TransactionRequest(accountNumber, TransactionType.DEPOSIT, depositAmount, "Salary deposit");

        Account account = createMockAccount("acc-001", accountNumber, mockCustomer, currentBalance, true);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId("tx-001");
        savedTransaction.setAccount(account);
        savedTransaction.setType(TransactionType.DEPOSIT);
        savedTransaction.setAmount(depositAmount);
        savedTransaction.setBalanceAfter(currentBalance.add(depositAmount));
        savedTransaction.setTimestamp(LocalDateTime.now());
        savedTransaction.setDescription("Salary deposit");

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // Act
        TransactionResponse response = transactionService.createTransaction(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("tx-001");
        assertThat(response.type()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(response.amount()).isEqualByComparingTo(depositAmount);
        assertThat(response.balanceAfter()).isEqualByComparingTo(new BigDecimal("1500.00"));

        verify(accountRepository).findByAccountNumber(accountNumber);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debe crear un retiro exitosamente")
    void createTransaction_Withdraw_Success() {
        // Arrange
        String accountNumber = "1234567890";
        BigDecimal currentBalance = new BigDecimal("1000.00");
        BigDecimal withdrawAmount = new BigDecimal("300.00");
        TransactionRequest request = new TransactionRequest(accountNumber, TransactionType.WITHDRAW, withdrawAmount, "Cash withdrawal");

        Account account = createMockAccount("acc-001", accountNumber, mockCustomer, currentBalance, true);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId("tx-002");
        savedTransaction.setAccount(account);
        savedTransaction.setType(TransactionType.WITHDRAW);
        savedTransaction.setAmount(withdrawAmount);
        savedTransaction.setBalanceAfter(currentBalance.subtract(withdrawAmount));
        savedTransaction.setTimestamp(LocalDateTime.now());
        savedTransaction.setDescription("Cash withdrawal");

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // Act
        TransactionResponse response = transactionService.createTransaction(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.type()).isEqualTo(TransactionType.WITHDRAW);
        assertThat(response.balanceAfter()).isEqualByComparingTo(new BigDecimal("700.00"));
    }

    @Test
    @DisplayName("Debe rechazar cuenta inexistente")
    void createTransaction_NonExistingAccount_ThrowsException() {
        // Arrange
        TransactionRequest request = new TransactionRequest("9999999999", TransactionType.DEPOSIT, new BigDecimal("100.00"), "Test");
        when(accountRepository.findByAccountNumber("9999999999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("not found");

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debe rechazar cuenta inactiva")
    void createTransaction_InactiveAccount_ThrowsException() {
        // Arrange
        TransactionRequest request = new TransactionRequest("1234567890", TransactionType.DEPOSIT, new BigDecimal("100.00"), "Test");
        Account inactiveAccount = createMockAccount("acc-001", "1234567890", mockCustomer, new BigDecimal("1000.00"), false);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(inactiveAccount));

        // Act & Assert
        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(InactiveAccountException.class)
                .hasMessageContaining("inactive account");

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debe rechazar retiro con saldo insuficiente")
    void createTransaction_InsufficientBalance_ThrowsException() {
        // Arrange
        TransactionRequest request = new TransactionRequest("1234567890", TransactionType.WITHDRAW, new BigDecimal("2000.00"), "Overdraft attempt");
        Account account = createMockAccount("acc-001", "1234567890", mockCustomer, new BigDecimal("1000.00"), true);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(account));

        // Act & Assert
        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Insufficient balance");

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debe obtener transacción por ID")
    void getTransactionById_ExistingId_Success() {
        // Arrange
        Account account = createMockAccount("acc-001", "1234567890", mockCustomer, new BigDecimal("1000.00"), true);

        Transaction transaction = new Transaction();
        transaction.setId("tx-001");
        transaction.setAccount(account);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setBalanceAfter(new BigDecimal("1500.00"));
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setDescription("Test deposit");

        when(transactionRepository.findById("tx-001")).thenReturn(Optional.of(transaction));

        // Act
        TransactionResponse response = transactionService.getTransactionById("tx-001");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("tx-001");
        assertThat(response.type()).isEqualTo(TransactionType.DEPOSIT);
    }

    @Test
    @DisplayName("Debe lanzar excepción si ID no existe")
    void getTransactionById_NonExistingId_ThrowsException() {
        // Arrange
        when(transactionRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> transactionService.getTransactionById("invalid-id"))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Debe listar todas las transacciones")
    void getAllTransactions_MultipleTransactions_Success() {
        // Arrange
        Account account = createMockAccount("acc-001", "1234567890", mockCustomer, new BigDecimal("1000.00"), true);

        Transaction transaction1 = new Transaction();
        transaction1.setId("tx-001");
        transaction1.setAccount(account);
        transaction1.setType(TransactionType.DEPOSIT);
        transaction1.setAmount(new BigDecimal("500.00"));
        transaction1.setBalanceAfter(new BigDecimal("1500.00"));
        transaction1.setTimestamp(LocalDateTime.now());

        Transaction transaction2 = new Transaction();
        transaction2.setId("tx-002");
        transaction2.setAccount(account);
        transaction2.setType(TransactionType.WITHDRAW);
        transaction2.setAmount(new BigDecimal("200.00"));
        transaction2.setBalanceAfter(new BigDecimal("1300.00"));
        transaction2.setTimestamp(LocalDateTime.now());

        when(transactionRepository.findAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        // Act
        List<TransactionResponse> responses = transactionService.getAllTransactions();

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo("tx-001");
        assertThat(responses.get(1).id()).isEqualTo("tx-002");
    }

    @Test
    @DisplayName("Debe filtrar transacciones por número de cuenta")
    void getTransactionsByAccountNumber_ExistingAccount_Success() {
        // Arrange
        Account account = createMockAccount("acc-001", "1234567890", mockCustomer, new BigDecimal("1000.00"), true);

        Transaction transaction = new Transaction();
        transaction.setId("tx-001");
        transaction.setAccount(account);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setBalanceAfter(new BigDecimal("1500.00"));
        transaction.setTimestamp(LocalDateTime.now());

        when(transactionRepository.findByAccountNumberOrderByTimestampDesc("1234567890"))
                .thenReturn(Arrays.asList(transaction));

        // Act
        List<TransactionResponse> responses = transactionService.getTransactionsByAccountNumber("1234567890");

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).accountNumber()).isEqualTo("1234567890");
    }
}
