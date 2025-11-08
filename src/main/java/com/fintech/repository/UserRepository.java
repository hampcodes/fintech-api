package com.fintech.repository;

import com.fintech.model.RoleType;
import com.fintech.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // ==================== MÉTODOS PARA REPORTES ====================

    // Contar usuarios por estado activo
    long countByActive(Boolean active);

    // Contar usuarios por rol
    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleName")
    long countByRoleName(RoleType roleName);

    // Contar nuevos usuarios en un rango de fechas
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    long countNewUsersByDateRange(
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate
    );

    // Contar usuarios activos que han tenido actividad en un rango de fechas
    @Query("SELECT COUNT(DISTINCT t.account.customer.user.id) FROM Transaction t " +
           "WHERE t.timestamp BETWEEN :startDate AND :endDate")
    long countActiveUsersByDateRange(
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate
    );
}
