package com.fintech.service;

import com.fintech.dto.response.AccountStatisticsResponse;
import com.fintech.dto.response.DashboardStatisticsResponse;
import com.fintech.dto.response.TransactionStatisticsResponse;
import com.fintech.dto.response.UserStatisticsResponse;
import com.fintech.model.RoleType;
import com.fintech.model.TransactionType;
import com.fintech.repository.AccountRepository;
import com.fintech.repository.TransactionRepository;
import com.fintech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public DashboardStatisticsResponse getDashboardStatistics() {
        return new DashboardStatisticsResponse(
                getUserStatistics(),
                getAccountStatistics(),
                getTransactionStatistics()
        );
    }

    @Transactional(readOnly = true)
    public UserStatisticsResponse getUserStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActive(true);
        long inactiveUsers = userRepository.countByActive(false);
        long adminUsers = userRepository.countByRoleName(RoleType.ROLE_ADMIN);
        long regularUsers = userRepository.countByRoleName(RoleType.ROLE_USER);

        return new UserStatisticsResponse(
                totalUsers,
                activeUsers,
                inactiveUsers,
                adminUsers,
                regularUsers
        );
    }

    @Transactional(readOnly = true)
    public AccountStatisticsResponse getAccountStatistics() {
        long totalAccounts = accountRepository.count();
        long activeAccounts = accountRepository.countByActive(true);
        long inactiveAccounts = accountRepository.countByActive(false);

        BigDecimal totalBalance = accountRepository.findAll()
                .stream()
                .map(account -> account.getBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageBalance = totalAccounts > 0
                ? totalBalance.divide(BigDecimal.valueOf(totalAccounts), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new AccountStatisticsResponse(
                totalAccounts,
                activeAccounts,
                inactiveAccounts,
                totalBalance,
                averageBalance
        );
    }

    @Transactional(readOnly = true)
    public TransactionStatisticsResponse getTransactionStatistics() {
        long totalTransactions = transactionRepository.count();
        long totalDeposits = transactionRepository.countByType(TransactionType.DEPOSIT);
        long totalWithdrawals = transactionRepository.countByType(TransactionType.WITHDRAW);

        BigDecimal totalDepositAmount = transactionRepository.findAll()
                .stream()
                .filter(t -> t.getType() == TransactionType.DEPOSIT)
                .map(t -> t.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithdrawalAmount = transactionRepository.findAll()
                .stream()
                .filter(t -> t.getType() == TransactionType.WITHDRAW)
                .map(t -> t.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netCashFlow = totalDepositAmount.subtract(totalWithdrawalAmount);

        return new TransactionStatisticsResponse(
                totalTransactions,
                totalDeposits,
                totalWithdrawals,
                totalDepositAmount,
                totalWithdrawalAmount,
                netCashFlow
        );
    }
}
