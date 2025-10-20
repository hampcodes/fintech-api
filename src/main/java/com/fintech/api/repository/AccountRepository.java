package com.fintech.api.repository;

import com.fintech.api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByActive(Boolean active);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findByOwnerEmailContainingIgnoreCase(String email);
}
