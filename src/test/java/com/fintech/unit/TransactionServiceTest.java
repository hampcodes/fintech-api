package com.fintech.unit;

import com.fintech.api.dto.request.TransactionRequest;
import com.fintech.api.dto.response.TransactionResponse;
import com.fintech.api.exception.AccountNotFoundException;
import com.fintech.api.exception.InactiveAccountException;
import com.fintech.api.exception.TransactionNotFoundException;
import com.fintech.api.model.Account;
import com.fintech.api.model.Transaction;
import com.fintech.api.model.TransactionType;
import com.fintech.api.repository.TransactionRepository;
import com.fintech.api.service.AccountService;
import com.fintech.api.service.TransactionService;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService - Pruebas Unitarias")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    private Account testAccount;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testAccount = new Account("1234567890", "John Doe",
                "john@example.com", new BigDecimal("1000.00"));
        testAccount.setId("acc-001");
        testAccount.setActive(true);

        testTransaction = new Transaction();
        testTransaction.setId("tx-001");
        testTransaction.setAccount(testAccount);
        testTransaction.setType(TransactionType.DEPOSIT);
        testTransaction.setAmount(new BigDecimal("500.00"));
        testTransaction.setBalanceAfter(new BigDecimal("1500.00"));
        testTransaction.setTimestamp(LocalDateTime.now());
        testTransaction.setDescription("Test deposit");
    }

    @Test
    @DisplayName("Debe crear un depósito exitosamente")
    void createTransaction_Deposit_Success() {
        // Given - Cuenta activa con $1000
        TransactionRequest request = new TransactionRequest(
                "1234567890",
                TransactionType.DEPOSIT,
                new BigDecimal("500.00"),
                "Salary deposit"
        );

        given(accountService.findAccountByNumber("1234567890")).willReturn(testAccount);
        given(transactionRepository.save(any(Transaction.class))).willReturn(testTransaction);

        // When - Se realiza un depósito
        TransactionResponse response = transactionService.createTransaction(request);

        // Then - La transacción se crea y el saldo aumenta
        assertThat(response).isNotNull();
        assertThat(response.type()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(response.balanceAfter()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(testAccount.getBalance()).isEqualByComparingTo(new BigDecimal("1500.00"));

        then(accountService).should(times(1)).findAccountByNumber("1234567890");
        then(transactionRepository).should(times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debe crear un retiro exitosamente")
    void createTransaction_Withdraw_Success() {
        // Given - Cuenta activa con $1000
        TransactionRequest request = new TransactionRequest(
                "1234567890",
                TransactionType.WITHDRAW,
                new BigDecimal("300.00"),
                "Cash withdrawal"
        );

        Transaction withdrawalTransaction = new Transaction();
        withdrawalTransaction.setId("tx-002");
        withdrawalTransaction.setAccount(testAccount);
        withdrawalTransaction.setType(TransactionType.WITHDRAW);
        withdrawalTransaction.setAmount(new BigDecimal("300.00"));
        withdrawalTransaction.setBalanceAfter(new BigDecimal("700.00"));
        withdrawalTransaction.setTimestamp(LocalDateTime.now());

        given(accountService.findAccountByNumber("1234567890")).willReturn(testAccount);
        given(transactionRepository.save(any(Transaction.class))).willReturn(withdrawalTransaction);

        // When - Se realiza un retiro
        TransactionResponse response = transactionService.createTransaction(request);

        // Then - La transacción se crea y el saldo disminuye
        assertThat(response).isNotNull();
        assertThat(response.type()).isEqualTo(TransactionType.WITHDRAW);
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(response.balanceAfter()).isEqualByComparingTo(new BigDecimal("700.00"));
        assertThat(testAccount.getBalance()).isEqualByComparingTo(new BigDecimal("700.00"));

        then(accountService).should(times(1)).findAccountByNumber("1234567890");
        then(transactionRepository).should(times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debe rechazar monto cero o negativo")
    void createTransaction_InvalidAmount_ThrowsException() {
        // Given - Monto inválido (cero)
        TransactionRequest request = new TransactionRequest(
                "1234567890",
                TransactionType.DEPOSIT,
                new BigDecimal("0.00"),
                "Invalid amount"
        );

        // When & Then - Se lanza excepción
        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be greater than zero");

        then(transactionRepository).should(times(0)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debe rechazar cuenta inexistente")
    void createTransaction_NonExistingAccount_ThrowsException() {
        // Given - Cuenta que no existe
        TransactionRequest request = new TransactionRequest(
                "9999999999",
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                "Test"
        );

        given(accountService.findAccountByNumber("9999999999"))
                .willThrow(new AccountNotFoundException("Account not found"));

        // When & Then - Se lanza excepción
        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("not found");

        then(transactionRepository).should(times(0)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debe rechazar cuenta inactiva")
    void createTransaction_InactiveAccount_ThrowsException() {
        // Given - Cuenta inactiva
        testAccount.deactivate();
        TransactionRequest request = new TransactionRequest(
                "1234567890",
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                "Test"
        );

        given(accountService.findAccountByNumber("1234567890")).willReturn(testAccount);

        // When & Then - Se lanza excepción
        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(InactiveAccountException.class)
                .hasMessageContaining("inactive account");

        then(transactionRepository).should(times(0)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debe rechazar retiro con saldo insuficiente")
    void createTransaction_Withdraw_ThrowsException_WhenInsufficientBalance() {
        // Given - Retiro mayor al saldo disponible
        TransactionRequest request = new TransactionRequest(
                "1234567890",
                TransactionType.WITHDRAW,
                new BigDecimal("2000.00"),
                "Overdraft attempt"
        );

        given(accountService.findAccountByNumber("1234567890")).willReturn(testAccount);

        // When & Then - Se lanza excepción
        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient balance");

        then(transactionRepository).should(times(0)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debe obtener transacción por ID")
    void getTransactionById_ExistingId_Success() {
        // Given - Transacción existente
        given(transactionRepository.findById("tx-001")).willReturn(Optional.of(testTransaction));

        // When - Se busca por ID
        TransactionResponse response = transactionService.getTransactionById("tx-001");

        // Then - Se retorna la transacción
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("tx-001");
        assertThat(response.accountNumber()).isEqualTo("1234567890");

        then(transactionRepository).should(times(1)).findById("tx-001");
    }

    @Test
    @DisplayName("Debe lanzar excepción si ID no existe")
    void getTransactionById_NonExistingId_ThrowsException() {
        // Given - ID inexistente
        given(transactionRepository.findById("invalid-id")).willReturn(Optional.empty());

        // When & Then - Se lanza excepción
        assertThatThrownBy(() -> transactionService.getTransactionById("invalid-id"))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining("not found");

        then(transactionRepository).should(times(1)).findById("invalid-id");
    }

    @Test
    @DisplayName("Debe listar todas las transacciones")
    void getAllTransactions_MultipleTransactions_Success() {
        // Given - Múltiples transacciones
        Transaction transaction2 = new Transaction();
        transaction2.setId("tx-002");
        transaction2.setAccount(testAccount);
        transaction2.setType(TransactionType.WITHDRAW);
        transaction2.setAmount(new BigDecimal("200.00"));
        transaction2.setBalanceAfter(new BigDecimal("800.00"));
        transaction2.setTimestamp(LocalDateTime.now());

        List<Transaction> transactions = Arrays.asList(testTransaction, transaction2);
        given(transactionRepository.findAll()).willReturn(transactions);

        // When - Se solicitan todas las transacciones
        List<TransactionResponse> responses = transactionService.getAllTransactions();

        // Then - Se retornan todas
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);

        then(transactionRepository).should(times(1)).findAll();
    }

    @Test
    @DisplayName("Debe filtrar transacciones por número de cuenta")
    void getTransactionsByAccountNumber_ExistingAccount_Success() {
        // Given - Transacciones de una cuenta específica
        List<Transaction> transactions = Arrays.asList(testTransaction);
        given(transactionRepository.findByAccountNumberOrderByTimestampDesc("1234567890"))
                .willReturn(transactions);

        // When - Se filtran por número de cuenta
        List<TransactionResponse> responses = transactionService
                .getTransactionsByAccountNumber("1234567890");

        // Then - Se retornan solo las de esa cuenta
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).accountNumber()).isEqualTo("1234567890");

        then(transactionRepository).should(times(1))
                .findByAccountNumberOrderByTimestampDesc("1234567890");
    }
}
