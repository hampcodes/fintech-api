package com.fintech.api.service;

import com.fintech.api.dto.request.AccountRequest;
import com.fintech.api.dto.response.AccountResponse;
import com.fintech.api.exception.AccountNotFoundException;
import com.fintech.api.exception.DuplicateAccountException;
import com.fintech.api.model.Account;
import com.fintech.api.repository.AccountRepository;
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
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        log.info("Creating account with number: {}", request.accountNumber());

        if (accountRepository.existsByAccountNumber(request.accountNumber())) {
            throw new DuplicateAccountException(
                    "Account with number " + request.accountNumber() + " already exists");
        }

        Account account = new Account(
                request.accountNumber(),
                request.ownerName(),
                request.ownerEmail(),
                request.initialBalance()
        );

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with ID: {}", savedAccount.getId());

        return mapToResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(String id) {
        log.info("Fetching account by ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
        return mapToResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber) {
        log.info("Fetching account by number: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with number: " + accountNumber));
        return mapToResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        log.info("Fetching all accounts");
        return accountRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getActiveAccounts() {
        log.info("Fetching active accounts");
        return accountRepository.findByActive(true)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponse deactivateAccount(String id) {
        log.info("Deactivating account with ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));

        account.deactivate();
        Account updatedAccount = accountRepository.save(account);
        log.info("Account deactivated successfully: {}", id);

        return mapToResponse(updatedAccount);
    }

    @Transactional
    public AccountResponse activateAccount(String id) {
        log.info("Activating account with ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));

        account.activate();
        Account updatedAccount = accountRepository.save(account);
        log.info("Account activated successfully: {}", id);

        return mapToResponse(updatedAccount);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAccountBalance(String accountNumber) {
        log.info("Fetching balance for account: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with number: " + accountNumber));
        return account.getBalance();
    }

    // Internal method used by TransactionService
    @Transactional
    public Account findAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with number: " + accountNumber));
    }

    private AccountResponse mapToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getOwnerName(),
                account.getOwnerEmail(),
                account.getBalance(),
                account.getActive(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
