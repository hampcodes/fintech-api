package com.fintech.api.repository;

import com.fintech.api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountIdOrderByTimestampDesc(String accountId);

    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountNumberOrderByTimestampDesc(String accountNumber);
}
