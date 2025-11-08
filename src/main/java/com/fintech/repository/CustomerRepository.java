package com.fintech.repository;

import com.fintech.model.Customer;
import com.fintech.model.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    @Query("SELECT c FROM Customer c WHERE c.user.id = :userId")
    Optional<Customer> findByUserId(@Param("userId") String userId);

    boolean existsByDni(String dni);
    boolean existsByPhone(String phone);

    // Para reportes
    long countByKycStatus(KycStatus kycStatus);
    long countByActive(Boolean active);
}
