package com.fintech.controller;

import com.fintech.dto.response.report.*;
import com.fintech.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Tag(name = "Reports", description = "API de reportes y estadísticas para gráficos")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ==================== 1. REPORTES DE TRANSACCIONES ====================

    @Operation(summary = "Obtener reporte de transacciones por período (para gráficos de líneas/barras)")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/transactions/by-period")
    public ResponseEntity<List<TransactionReportDTO>> getTransactionReportByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<TransactionReportDTO> report = reportService.getTransactionReportByPeriod(start, end);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Obtener transacciones agrupadas por tipo (para gráfico de pie/dona)")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/transactions/by-type")
    public ResponseEntity<List<TransactionByTypeDTO>> getTransactionsByType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<TransactionByTypeDTO> report = reportService.getTransactionsByType(start, end);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Obtener top cuentas por cantidad de transacciones")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/transactions/top-accounts")
    public ResponseEntity<List<TransactionByAccountDTO>> getTopAccountsByTransactionCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<TransactionByAccountDTO> report = reportService.getTopAccountsByTransactionCount(start, end, limit);
        return ResponseEntity.ok(report);
    }

    // ==================== 2. REPORTES DE CUENTAS ====================

    @Operation(summary = "Obtener distribución de saldos por rangos (para histogramas)")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/accounts/balance-distribution")
    public ResponseEntity<List<BalanceDistributionDTO>> getBalanceDistribution() {
        List<BalanceDistributionDTO> report = reportService.getBalanceDistribution();
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Obtener top cuentas por saldo")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/accounts/top-by-balance")
    public ResponseEntity<List<TopAccountsByBalanceDTO>> getTopAccountsByBalance(
            @RequestParam(defaultValue = "10") int limit) {
        List<TopAccountsByBalanceDTO> report = reportService.getTopAccountsByBalance(limit);
        return ResponseEntity.ok(report);
    }

    // ==================== 3. REPORTES DE ACTIVIDAD DE USUARIOS ====================

    @Operation(summary = "Obtener usuarios más activos por transacciones")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/users/top-by-activity")
    public ResponseEntity<List<UserActivityDTO>> getTopUsersByActivity(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<UserActivityDTO> report = reportService.getTopUsersByActivity(start, end, limit);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Obtener crecimiento de usuarios por período")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/users/growth")
    public ResponseEntity<List<UserGrowthDTO>> getUserGrowthByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<UserGrowthDTO> report = reportService.getUserGrowthByPeriod(start, end);
        return ResponseEntity.ok(report);
    }

    // ==================== 4. DASHBOARD GENERAL ====================

    @Operation(summary = "Obtener métricas del dashboard principal")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics() {
        DashboardMetricsDTO metrics = reportService.getDashboardMetrics();
        return ResponseEntity.ok(metrics);
    }

    // ==================== 5. REPORTES DE COMPARACIÓN TEMPORAL ====================

    @Operation(summary = "Comparar dos períodos de tiempo",
            description = "Compara métricas entre dos períodos. Útil para comparar mes actual vs anterior, semana actual vs anterior, etc.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/comparison")
    public ResponseEntity<PeriodComparisonDTO> getPeriodComparison(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentStartDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentEndDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousStartDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousEndDate) {
        LocalDateTime currentStart = currentStartDate.atStartOfDay();
        LocalDateTime currentEnd = currentEndDate.atTime(LocalTime.MAX);
        LocalDateTime previousStart = previousStartDate.atStartOfDay();
        LocalDateTime previousEnd = previousEndDate.atTime(LocalTime.MAX);

        PeriodComparisonDTO comparison = reportService.getPeriodComparison(
                currentStart, currentEnd, previousStart, previousEnd);
        return ResponseEntity.ok(comparison);
    }

    // ==================== ENDPOINTS HELPER PARA REPORTES RÁPIDOS ====================

    @Operation(summary = "Obtener reportes del mes actual")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/current-month")
    public ResponseEntity<List<TransactionReportDTO>> getCurrentMonthReport() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDateTime start = startOfMonth.atStartOfDay();
        LocalDateTime end = now.atTime(LocalTime.MAX);
        List<TransactionReportDTO> report = reportService.getTransactionReportByPeriod(start, end);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Obtener reportes de la semana actual")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/current-week")
    public ResponseEntity<List<TransactionReportDTO>> getCurrentWeekReport() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDateTime start = startOfWeek.atStartOfDay();
        LocalDateTime end = now.atTime(LocalTime.MAX);
        List<TransactionReportDTO> report = reportService.getTransactionReportByPeriod(start, end);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Obtener reportes de hoy")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/today")
    public ResponseEntity<List<TransactionReportDTO>> getTodayReport() {
        LocalDate now = LocalDate.now();
        LocalDateTime start = now.atStartOfDay();
        LocalDateTime end = now.atTime(LocalTime.MAX);
        List<TransactionReportDTO> report = reportService.getTransactionReportByPeriod(start, end);
        return ResponseEntity.ok(report);
    }
}
