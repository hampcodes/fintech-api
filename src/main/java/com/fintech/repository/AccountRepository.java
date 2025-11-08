package com.fintech.repository;

import com.fintech.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByActive(Boolean active);

    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Account a WHERE a.accountNumber = :accountNumber AND a.customer.id = :customerId")
    boolean existsByAccountNumberAndCustomerId(@Param("accountNumber") String accountNumber, @Param("customerId") String customerId);

    // Métodos con paginación
    @Query("SELECT a FROM Account a WHERE a.customer.id = :customerId")
    Page<Account> findByCustomerId(@Param("customerId") String customerId, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.customer.id = :customerId AND a.active = :active")
    Page<Account> findByCustomerIdAndActive(@Param("customerId") String customerId, @Param("active") Boolean active, Pageable pageable);

    // Para admin - sin filtro de customer
    Page<Account> findByActive(Boolean active, Pageable pageable);

    // ==================== MÉTODOS PARA REPORTES ====================

    // Contar cuentas por estado activo
    long countByActive(Boolean active);

    // Obtener suma total de balances
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.active = true")
    Double getTotalBalance();

    // Obtener promedio de balances
    @Query("SELECT AVG(a.balance) FROM Account a WHERE a.active = true")
    Double getAverageBalance();

    // Top cuentas por balance
    @Query("SELECT a FROM Account a WHERE a.active = true ORDER BY a.balance DESC")
    List<Account> findTopAccountsByBalance(Pageable pageable);

    // Distribución de balances por rangos
    @Query("SELECT CASE " +
           "WHEN a.balance < 1000 THEN '0-1000' " +
           "WHEN a.balance >= 1000 AND a.balance < 5000 THEN '1000-5000' " +
           "WHEN a.balance >= 5000 AND a.balance < 10000 THEN '5000-10000' " +
           "WHEN a.balance >= 10000 AND a.balance < 50000 THEN '10000-50000' " +
           "ELSE '50000+' END as range, " +
           "COUNT(a), SUM(a.balance) " +
           "FROM Account a " +
           "WHERE a.active = true " +
           "GROUP BY CASE " +
           "WHEN a.balance < 1000 THEN '0-1000' " +
           "WHEN a.balance >= 1000 AND a.balance < 5000 THEN '1000-5000' " +
           "WHEN a.balance >= 5000 AND a.balance < 10000 THEN '5000-10000' " +
           "WHEN a.balance >= 10000 AND a.balance < 50000 THEN '10000-50000' " +
           "ELSE '50000+' END " +
           "ORDER BY MIN(a.balance)")
    List<Object[]> findBalanceDistribution();
}
