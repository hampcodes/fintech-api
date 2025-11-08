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

    // Métodos para reportes
    long countByActive(Boolean active);
}
