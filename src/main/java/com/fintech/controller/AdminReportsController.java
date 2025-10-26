package com.fintech.controller;

import com.fintech.dto.response.AccountStatisticsResponse;
import com.fintech.dto.response.DashboardStatisticsResponse;
import com.fintech.dto.response.TransactionStatisticsResponse;
import com.fintech.dto.response.UserStatisticsResponse;
import com.fintech.service.ReportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - Reports", description = "API de reportes y estadísticas (solo ADMIN)")
@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportsController {

    private final ReportsService reportsService;

    @Operation(summary = "Obtener dashboard completo con todas las estadísticas")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatisticsResponse> getDashboard() {
        DashboardStatisticsResponse stats = reportsService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Obtener estadísticas de usuarios")
    @GetMapping("/users")
    public ResponseEntity<UserStatisticsResponse> getUserStatistics() {
        UserStatisticsResponse stats = reportsService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Obtener estadísticas de cuentas")
    @GetMapping("/accounts")
    public ResponseEntity<AccountStatisticsResponse> getAccountStatistics() {
        AccountStatisticsResponse stats = reportsService.getAccountStatistics();
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Obtener estadísticas de transacciones")
    @GetMapping("/transactions")
    public ResponseEntity<TransactionStatisticsResponse> getTransactionStatistics() {
        TransactionStatisticsResponse stats = reportsService.getTransactionStatistics();
        return ResponseEntity.ok(stats);
    }
}
