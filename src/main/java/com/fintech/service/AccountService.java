package com.fintech.service;

import com.fintech.dto.request.AccountRequest;
import com.fintech.dto.response.AccountResponse;
import com.fintech.exception.AccountNotFoundException;
import com.fintech.exception.DuplicateAccountException;
import com.fintech.exception.UnauthorizedAccessException;
import com.fintech.model.Account;
import com.fintech.model.Customer;
import com.fintech.model.User;
import com.fintech.repository.AccountRepository;
import com.fintech.repository.CustomerRepository;
import com.fintech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        if (accountRepository.existsByAccountNumber(request.accountNumber())) {
            throw new DuplicateAccountException(
                    "Account with number " + request.accountNumber() + " already exists");
        }

        Customer customer = getAuthenticatedCustomer();

        Account account = new Account();
        account.setAccountNumber(request.accountNumber());
        account.setCustomer(customer);
        account.setBalance(request.initialBalance());

        return mapToResponse(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
        validateOwnership(account);
        return mapToResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with number: " + accountNumber));
        validateOwnership(account);
        return mapToResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        Customer currentCustomer = getAuthenticatedCustomer();
        return accountRepository.findAll()
                .stream()
                .filter(account -> account.getCustomer().getId().equals(currentCustomer.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getActiveAccounts() {
        Customer currentCustomer = getAuthenticatedCustomer();
        return accountRepository.findByActive(true)
                .stream()
                .filter(account -> account.getCustomer().getId().equals(currentCustomer.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponse deactivateAccount(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
        validateOwnership(account);
        account.setActive(false);
        return mapToResponse(accountRepository.save(account));
    }

    @Transactional
    public AccountResponse activateAccount(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
        validateOwnership(account);
        account.setActive(true);
        return mapToResponse(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public BigDecimal getAccountBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with number: " + accountNumber));
        validateOwnership(account);
        return account.getBalance();
    }

    // Internal method used by TransactionService
    @Transactional
    public Account findAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with number: " + accountNumber));
    }

    private Customer getAuthenticatedCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));
        return customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found for user"));
    }

    private void validateOwnership(Account account) {
        Customer currentCustomer = getAuthenticatedCustomer();
        if (!account.getCustomer().getId().equals(currentCustomer.getId())) {
            throw new UnauthorizedAccessException("You don't have permission to access this account");
        }
    }

    private AccountResponse mapToResponse(Account account) {
        Customer customer = account.getCustomer();
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                customer.getId(),
                customer.getName(),
                account.getBalance(),
                account.getActive(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    // ==================== ADMIN METHODS (sin validación de ownership) ====================

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccountsAdmin() {
        return accountRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountByIdAdmin(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
        return mapToResponse(account);
    }

    @Transactional
    public AccountResponse deactivateAccountAdmin(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
        account.setActive(false);
        return mapToResponse(accountRepository.save(account));
    }

    @Transactional
    public AccountResponse activateAccountAdmin(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
        account.setActive(true);
        return mapToResponse(accountRepository.save(account));
    }
}
