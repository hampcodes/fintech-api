package com.fintech.service;

import com.fintech.dto.request.TransactionRequest;
import com.fintech.dto.response.TransactionResponse;
import com.fintech.exception.AccountNotFoundException;
import com.fintech.exception.DailyLimitExceededException;
import com.fintech.exception.InactiveAccountException;
import com.fintech.exception.InsufficientBalanceException;
import com.fintech.exception.InvalidTransactionAmountException;
import com.fintech.exception.TransactionNotFoundException;
import com.fintech.model.Account;
import com.fintech.model.Transaction;
import com.fintech.model.TransactionType;
import com.fintech.repository.AccountRepository;
import com.fintech.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final SettingsService settingsService;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        // Validate transaction amount against settings
        validateMinAmount(request.amount());
        validateMaxAmount(request.amount());

        Account account = accountRepository.findByAccountNumber(request.accountNumber())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with number: " + request.accountNumber()));

        if (!account.getActive()) {
            throw new InactiveAccountException(
                    "Cannot perform transaction on inactive account: " + request.accountNumber());
        }

        // Validate daily withdrawal limit for withdrawals
        if (request.type() == TransactionType.WITHDRAW) {
            validateDailyWithdrawalLimit(account, request.amount());
        }

        BigDecimal balanceAfter;
        if (request.type() == TransactionType.DEPOSIT) {
            balanceAfter = account.getBalance().add(request.amount());
            account.setBalance(balanceAfter);
        } else if (request.type() == TransactionType.WITHDRAW) {
            if (account.getBalance().compareTo(request.amount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }
            balanceAfter = account.getBalance().subtract(request.amount());
            account.setBalance(balanceAfter);
        } else {
            throw new InvalidTransactionAmountException("Invalid transaction type: " + request.type());
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setBalanceAfter(balanceAfter);

        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(String id) {
        return mapToResponse(transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + id)));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccountId(String accountId) {
        return transactionRepository.findByAccountIdOrderByTimestampDesc(accountId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccountNumber(String accountNumber) {
        return transactionRepository.findByAccountNumberOrderByTimestampDesc(accountNumber)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getAccount().getCustomer().getName(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getTimestamp(),
                transaction.getDescription()
        );
    }

    // ==================== VALIDATION METHODS WITH SETTINGS ====================

    private BigDecimal getSettingValue(String key, BigDecimal defaultValue) {
        try {
            var setting = settingsService.getSettingByKey(key);
            return new BigDecimal(setting.settingValue());
        } catch (Exception e) {
            log.warn("Setting {} not found, using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    private void validateMinAmount(BigDecimal amount) {
        BigDecimal minAmount = getSettingValue("MIN_TRANSACTION_AMOUNT", new BigDecimal("1.00"));
        if (amount.compareTo(minAmount) < 0) {
            throw new InvalidTransactionAmountException(
                    String.format("Transaction amount must be at least %s", minAmount)
            );
        }
    }

    private void validateMaxAmount(BigDecimal amount) {
        BigDecimal maxAmount = getSettingValue("MAX_TRANSACTION_AMOUNT", new BigDecimal("100000.00"));
        if (amount.compareTo(maxAmount) > 0) {
            throw new InvalidTransactionAmountException(
                    String.format("Transaction amount cannot exceed %s", maxAmount)
            );
        }
    }

    private void validateDailyWithdrawalLimit(Account account, BigDecimal amount) {
        BigDecimal maxDailyWithdrawal = getSettingValue("MAX_DAILY_WITHDRAWAL", new BigDecimal("5000.00"));

        // Get today's withdrawals
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        BigDecimal todayWithdrawals = transactionRepository
                .findByAccountAndTypeBetweenDates(
                        account.getAccountNumber(),
                        TransactionType.WITHDRAW,
                        startOfDay,
                        endOfDay
                )
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithToday = todayWithdrawals.add(amount);

        if (totalWithToday.compareTo(maxDailyWithdrawal) > 0) {
            throw new DailyLimitExceededException(
                    String.format("Daily withdrawal limit of %s exceeded. Current: %s, Attempted: %s",
                            maxDailyWithdrawal, todayWithdrawals, amount)
            );
        }

        log.info("Daily withdrawal validation passed for account {}. Total today: {}, Limit: {}",
                account.getAccountNumber(), totalWithToday, maxDailyWithdrawal);
    }

    // ==================== ADMIN METHODS ====================

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactionsAdmin() {
        return transactionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
