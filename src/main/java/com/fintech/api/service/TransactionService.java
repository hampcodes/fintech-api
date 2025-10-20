package com.fintech.api.service;

import com.fintech.api.dto.request.TransactionRequest;
import com.fintech.api.dto.response.TransactionResponse;
import com.fintech.api.exception.InactiveAccountException;
import com.fintech.api.exception.TransactionNotFoundException;
import com.fintech.api.model.Account;
import com.fintech.api.model.Transaction;
import com.fintech.api.model.TransactionType;
import com.fintech.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        log.info("Processing {} transaction for account: {}, amount: {}",
                request.type(), request.accountNumber(), request.amount());

        // Validate amount
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        }

        // Find account
        Account account = accountService.findAccountByNumber(request.accountNumber());

        // Check if account is active
        if (!account.getActive()) {
            throw new InactiveAccountException(
                    "Cannot perform transaction on inactive account: " + request.accountNumber());
        }

        // Process transaction based on type
        BigDecimal balanceAfter;
        if (request.type() == TransactionType.DEPOSIT) {
            account.deposit(request.amount());
            balanceAfter = account.getBalance();
            log.info("Deposit processed successfully. New balance: {}", balanceAfter);
        } else if (request.type() == TransactionType.WITHDRAW) {
            account.withdraw(request.amount());
            balanceAfter = account.getBalance();
            log.info("Withdrawal processed successfully. New balance: {}", balanceAfter);
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + request.type());
        }

        // Create transaction record
        Transaction transaction = new Transaction(
                account,
                request.type(),
                request.amount(),
                request.description(),
                balanceAfter
        );

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction saved with ID: {}", savedTransaction.getId());

        return mapToResponse(savedTransaction);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(String id) {
        log.info("Fetching transaction by ID: {}", id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + id));
        return mapToResponse(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        log.info("Fetching all transactions");
        return transactionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccountId(String accountId) {
        log.info("Fetching transactions for account ID: {}", accountId);
        return transactionRepository.findByAccountIdOrderByTimestampDesc(accountId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccountNumber(String accountNumber) {
        log.info("Fetching transactions for account number: {}", accountNumber);
        return transactionRepository.findByAccountNumberOrderByTimestampDesc(accountNumber)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getAccount().getOwnerName(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getTimestamp(),
                transaction.getDescription()
        );
    }
}
