package com.fintech.repository;

import com.fintech.model.Transaction;
import com.fintech.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountIdOrderByTimestampDesc(String accountId);

    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountNumberOrderByTimestampDesc(String accountNumber);

    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber " +
           "AND t.type = :type " +
           "AND t.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountAndTypeBetweenDates(
            @Param("accountNumber") String accountNumber,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Métodos con paginación
    @Query("SELECT t FROM Transaction t WHERE t.account.customer.id = :customerId ORDER BY t.timestamp DESC")
    Page<Transaction> findByCustomerId(@Param("customerId") String customerId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber ORDER BY t.timestamp DESC")
    Page<Transaction> findByAccountNumberPaginated(@Param("accountNumber") String accountNumber, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId ORDER BY t.timestamp DESC")
    Page<Transaction> findByAccountIdPaginated(@Param("accountId") String accountId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber " +
           "AND t.timestamp BETWEEN :startDate AND :endDate ORDER BY t.timestamp DESC")
    Page<Transaction> findByAccountNumberAndDateRange(
            @Param("accountNumber") String accountNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT t FROM Transaction t WHERE t.timestamp BETWEEN :startDate AND :endDate " +
           "AND t.account.customer.id = :customerId ORDER BY t.timestamp DESC")
    Page<Transaction> findByCustomerIdAndDateRange(
            @Param("customerId") String customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    // Para admin - sin filtro de customer
    @Query("SELECT t FROM Transaction t WHERE t.timestamp BETWEEN :startDate AND :endDate ORDER BY t.timestamp DESC")
    Page<Transaction> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    // ==================== MÉTODOS PARA REPORTES ====================

    // Contar transacciones por tipo
    long countByType(TransactionType type);

    // Contar transacciones en un rango de fechas
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.timestamp BETWEEN :startDate AND :endDate")
    long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Contar transacciones por tipo en un rango de fechas
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.type = :type AND t.timestamp BETWEEN :startDate AND :endDate")
    long countByTypeAndDateRange(
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Obtener transacciones agrupadas por fecha (para gráficos de línea/barras)
    @Query("SELECT DATE(t.timestamp) as date, COUNT(t) as count, SUM(t.amount) as total " +
           "FROM Transaction t " +
           "WHERE t.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(t.timestamp) " +
           "ORDER BY DATE(t.timestamp)")
    List<Object[]> findTransactionsByDateGrouped(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Top cuentas por volumen de transacciones
    @Query("SELECT t.account.accountNumber, t.account.customer.name, COUNT(t), SUM(t.amount) " +
           "FROM Transaction t " +
           "WHERE t.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY t.account.accountNumber, t.account.customer.name " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> findTopAccountsByTransactionCount(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    // Transacciones agrupadas por tipo
    @Query("SELECT t.type, COUNT(t), SUM(t.amount) " +
           "FROM Transaction t " +
           "WHERE t.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY t.type")
    List<Object[]> findTransactionsByTypeGrouped(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Usuarios más activos (por transacciones)
    @Query("SELECT t.account.customer.id, t.account.customer.name, COUNT(t), SUM(t.amount), MAX(t.timestamp) " +
           "FROM Transaction t " +
           "WHERE t.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY t.account.customer.id, t.account.customer.name " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> findTopUsersByActivity(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
