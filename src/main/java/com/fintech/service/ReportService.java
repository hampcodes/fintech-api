package com.fintech.service;

import com.fintech.dto.response.report.*;
import com.fintech.model.Account;
import com.fintech.model.TransactionType;
import com.fintech.repository.AccountRepository;
import com.fintech.repository.TransactionRepository;
import com.fintech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    // ==================== 1. REPORTES DE TRANSACCIONES ====================

    @Transactional(readOnly = true)
    public List<TransactionReportDTO> getTransactionReportByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating transaction report by period from {} to {}", startDate, endDate);

        List<TransactionReportDTO> reports = new ArrayList<>();
        List<Object[]> results = transactionRepository.findTransactionsByDateGrouped(startDate, endDate);

        for (Object[] row : results) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Long count = ((Number) row[1]).longValue();
            BigDecimal total = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;

            // Obtener datos por tipo para este día
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

            long deposits = transactionRepository.countByTypeAndDateRange(TransactionType.DEPOSIT, dayStart, dayEnd);
            long withdrawals = transactionRepository.countByTypeAndDateRange(TransactionType.WITHDRAW, dayStart, dayEnd);

            // Obtener montos por tipo
            List<Object[]> typeData = transactionRepository.findTransactionsByTypeGrouped(dayStart, dayEnd);
            BigDecimal depositAmount = BigDecimal.ZERO;
            BigDecimal withdrawalAmount = BigDecimal.ZERO;

            for (Object[] typeRow : typeData) {
                TransactionType type = (TransactionType) typeRow[0];
                BigDecimal amount = typeRow[2] != null ? new BigDecimal(typeRow[2].toString()) : BigDecimal.ZERO;
                if (type == TransactionType.DEPOSIT) {
                    depositAmount = amount;
                } else if (type == TransactionType.WITHDRAW) {
                    withdrawalAmount = amount;
                }
            }

            BigDecimal netCashFlow = depositAmount.subtract(withdrawalAmount);

            reports.add(new TransactionReportDTO(
                    date.toString(),
                    count,
                    deposits,
                    withdrawals,
                    depositAmount,
                    withdrawalAmount,
                    netCashFlow
            ));
        }

        return reports;
    }

    @Transactional(readOnly = true)
    public List<TransactionByTypeDTO> getTransactionsByType(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating transactions by type report from {} to {}", startDate, endDate);

        List<Object[]> results = transactionRepository.findTransactionsByTypeGrouped(startDate, endDate);
        List<TransactionByTypeDTO> reports = new ArrayList<>();

        long totalTransactions = transactionRepository.countByDateRange(startDate, endDate);

        for (Object[] row : results) {
            TransactionType type = (TransactionType) row[0];
            Long count = ((Number) row[1]).longValue();
            BigDecimal totalAmount = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            Double percentage = totalTransactions > 0 ? (count.doubleValue() / totalTransactions) * 100 : 0.0;

            reports.add(new TransactionByTypeDTO(type, count, totalAmount, percentage));
        }

        return reports;
    }

    @Transactional(readOnly = true)
    public List<TransactionByAccountDTO> getTopAccountsByTransactionCount(
            LocalDateTime startDate, LocalDateTime endDate, int limit) {
        log.info("Generating top {} accounts by transaction count from {} to {}", limit, startDate, endDate);

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = transactionRepository.findTopAccountsByTransactionCount(startDate, endDate, pageable);
        List<TransactionByAccountDTO> reports = new ArrayList<>();

        for (Object[] row : results) {
            String accountNumber = (String) row[0];
            String customerName = (String) row[1];
            Long count = ((Number) row[2]).longValue();
            BigDecimal totalAmount = row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO;

            reports.add(new TransactionByAccountDTO(accountNumber, customerName, count, totalAmount));
        }

        return reports;
    }

    // ==================== 2. REPORTES DE CUENTAS ====================

    @Transactional(readOnly = true)
    public List<BalanceDistributionDTO> getBalanceDistribution() {
        log.info("Generating balance distribution report");

        List<Object[]> results = accountRepository.findBalanceDistribution();
        List<BalanceDistributionDTO> reports = new ArrayList<>();

        for (Object[] row : results) {
            String range = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            BigDecimal totalBalance = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;

            reports.add(new BalanceDistributionDTO(range, count, totalBalance));
        }

        return reports;
    }

    @Transactional(readOnly = true)
    public List<TopAccountsByBalanceDTO> getTopAccountsByBalance(int limit) {
        log.info("Generating top {} accounts by balance", limit);

        Pageable pageable = PageRequest.of(0, limit);
        List<Account> accounts = accountRepository.findTopAccountsByBalance(pageable);
        List<TopAccountsByBalanceDTO> reports = new ArrayList<>();

        for (Account account : accounts) {
            // Obtener la última transacción
            List<com.fintech.model.Transaction> transactions =
                    transactionRepository.findByAccountIdOrderByTimestampDesc(account.getId());
            LocalDateTime lastTransaction = transactions.isEmpty() ? null : transactions.get(0).getTimestamp();

            reports.add(new TopAccountsByBalanceDTO(
                    account.getAccountNumber(),
                    account.getCustomer().getName(),
                    account.getBalance(),
                    lastTransaction
            ));
        }

        return reports;
    }

    // ==================== 3. REPORTES DE ACTIVIDAD DE USUARIOS ====================

    @Transactional(readOnly = true)
    public List<UserActivityDTO> getTopUsersByActivity(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        log.info("Generating top {} users by activity from {} to {}", limit, startDate, endDate);

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = transactionRepository.findTopUsersByActivity(startDate, endDate, pageable);
        List<UserActivityDTO> reports = new ArrayList<>();

        for (Object[] row : results) {
            String customerId = (String) row[0];
            String customerName = (String) row[1];
            Long transactionCount = ((Number) row[2]).longValue();
            BigDecimal totalVolume = row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO;
            LocalDateTime lastActivity = row[4] != null ? (LocalDateTime) row[4] : null;

            reports.add(new UserActivityDTO(customerId, customerName, transactionCount, totalVolume, lastActivity));
        }

        return reports;
    }

    @Transactional(readOnly = true)
    public List<UserGrowthDTO> getUserGrowthByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating user growth report from {} to {}", startDate, endDate);

        List<UserGrowthDTO> reports = new ArrayList<>();
        LocalDate currentDate = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();

        while (!currentDate.isAfter(end)) {
            LocalDateTime periodStart = currentDate.atStartOfDay();
            LocalDateTime periodEnd = currentDate.atTime(LocalTime.MAX);

            long newUsers = userRepository.countNewUsersByDateRange(periodStart, periodEnd);
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.countActiveUsersByDateRange(periodStart, periodEnd);

            reports.add(new UserGrowthDTO(
                    currentDate.toString(),
                    newUsers,
                    totalUsers,
                    activeUsers
            ));

            currentDate = currentDate.plusDays(1);
        }

        return reports;
    }

    // ==================== 4. DASHBOARD GENERAL ====================

    @Transactional(readOnly = true)
    public DashboardMetricsDTO getDashboardMetrics() {
        log.info("Generating dashboard metrics");

        // Métricas generales
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActive(true);
        long totalAccounts = accountRepository.count();
        long activeAccounts = accountRepository.countByActive(true);
        Double totalBalanceDouble = accountRepository.getTotalBalance();
        BigDecimal totalBalance = totalBalanceDouble != null ?
                BigDecimal.valueOf(totalBalanceDouble) : BigDecimal.ZERO;

        // Métricas de transacciones de hoy
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        long todayTransactions = transactionRepository.countByDateRange(todayStart, todayEnd);

        List<Object[]> todayData = transactionRepository.findTransactionsByTypeGrouped(todayStart, todayEnd);
        BigDecimal todayVolume = BigDecimal.ZERO;
        for (Object[] row : todayData) {
            BigDecimal amount = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            todayVolume = todayVolume.add(amount);
        }

        // Promedio de transacciones
        long totalTransactions = transactionRepository.count();
        Double avgTransactionAmount = totalTransactions > 0 ?
                todayVolume.divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP).doubleValue() : 0.0;

        // Calcular tasas de crecimiento (comparar con período anterior)
        LocalDateTime yesterdayStart = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime yesterdayEnd = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);

        long yesterdayTransactions = transactionRepository.countByDateRange(yesterdayStart, yesterdayEnd);
        List<Object[]> yesterdayData = transactionRepository.findTransactionsByTypeGrouped(yesterdayStart, yesterdayEnd);
        BigDecimal yesterdayVolume = BigDecimal.ZERO;
        for (Object[] row : yesterdayData) {
            BigDecimal amount = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            yesterdayVolume = yesterdayVolume.add(amount);
        }

        Double transactionGrowthRate = yesterdayTransactions > 0 ?
                ((todayTransactions - yesterdayTransactions) / (double) yesterdayTransactions) * 100 : 0.0;

        Double volumeGrowthRate = yesterdayVolume.compareTo(BigDecimal.ZERO) > 0 ?
                todayVolume.subtract(yesterdayVolume)
                        .divide(yesterdayVolume, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue() : 0.0;

        // User growth rate (comparar con semana anterior)
        LocalDateTime lastWeekStart = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime lastWeekEnd = LocalDate.now().minusDays(7).atTime(LocalTime.MAX);
        long lastWeekUsers = userRepository.countNewUsersByDateRange(lastWeekStart, lastWeekEnd);
        long thisWeekUsers = userRepository.countNewUsersByDateRange(todayStart, todayEnd);

        Double userGrowthRate = lastWeekUsers > 0 ?
                ((thisWeekUsers - lastWeekUsers) / (double) lastWeekUsers) * 100 : 0.0;

        return new DashboardMetricsDTO(
                totalUsers,
                activeUsers,
                totalAccounts,
                activeAccounts,
                totalBalance,
                totalTransactions,
                todayTransactions,
                todayVolume,
                BigDecimal.valueOf(avgTransactionAmount),
                userGrowthRate,
                transactionGrowthRate,
                volumeGrowthRate
        );
    }

    // ==================== 5. REPORTES DE COMPARACIÓN TEMPORAL ====================

    @Transactional(readOnly = true)
    public PeriodComparisonDTO getPeriodComparison(LocalDateTime currentStart, LocalDateTime currentEnd,
                                                    LocalDateTime previousStart, LocalDateTime previousEnd) {
        log.info("Generating period comparison: current({} to {}) vs previous({} to {})",
                currentStart, currentEnd, previousStart, previousEnd);

        // Métricas del período actual
        long currentTransactions = transactionRepository.countByDateRange(currentStart, currentEnd);
        List<Object[]> currentData = transactionRepository.findTransactionsByTypeGrouped(currentStart, currentEnd);
        BigDecimal currentVolume = BigDecimal.ZERO;
        for (Object[] row : currentData) {
            BigDecimal amount = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            currentVolume = currentVolume.add(amount);
        }
        long currentActiveUsers = userRepository.countActiveUsersByDateRange(currentStart, currentEnd);

        // Métricas del período anterior
        long previousTransactions = transactionRepository.countByDateRange(previousStart, previousEnd);
        List<Object[]> previousData = transactionRepository.findTransactionsByTypeGrouped(previousStart, previousEnd);
        BigDecimal previousVolume = BigDecimal.ZERO;
        for (Object[] row : previousData) {
            BigDecimal amount = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            previousVolume = previousVolume.add(amount);
        }
        long previousActiveUsers = userRepository.countActiveUsersByDateRange(previousStart, previousEnd);

        // Calcular cambios porcentuales
        Double transactionChangePercent = previousTransactions > 0 ?
                ((currentTransactions - previousTransactions) / (double) previousTransactions) * 100 : 0.0;

        Double volumeChangePercent = previousVolume.compareTo(BigDecimal.ZERO) > 0 ?
                currentVolume.subtract(previousVolume)
                        .divide(previousVolume, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue() : 0.0;

        Double activeUserChangePercent = previousActiveUsers > 0 ?
                ((currentActiveUsers - previousActiveUsers) / (double) previousActiveUsers) * 100 : 0.0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return new PeriodComparisonDTO(
                currentStart.toLocalDate().format(formatter) + " to " + currentEnd.toLocalDate().format(formatter),
                previousStart.toLocalDate().format(formatter) + " to " + previousEnd.toLocalDate().format(formatter),
                currentTransactions,
                currentVolume,
                currentActiveUsers,
                previousTransactions,
                previousVolume,
                previousActiveUsers,
                transactionChangePercent,
                volumeChangePercent,
                activeUserChangePercent
        );
    }
}
