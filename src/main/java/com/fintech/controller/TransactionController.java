package com.fintech.controller;

import com.fintech.dto.request.TransactionRequest;
import com.fintech.dto.response.TransactionResponse;
import com.fintech.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Transactions", description = "API de gestión de transacciones financieras")
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Crear nueva transacción (depósito o retiro)")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener transacción por ID")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable String id) {
        TransactionResponse response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todas las transacciones")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Listar transacciones por número de cuenta")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/account/number/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountNumber(@PathVariable String accountNumber) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Listar transacciones por ID de cuenta")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/account/id/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountId(@PathVariable String accountId) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    // ==================== PAGINATED ENDPOINTS ====================

    @Operation(summary = "Listar transacciones con paginación")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/paginated")
    public ResponseEntity<Page<TransactionResponse>> getAllTransactionsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TransactionResponse> transactions = transactionService.getAllTransactionsPaginated(pageable);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Listar transacciones por número de cuenta con paginación")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/paginated/account/number/{accountNumber}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByAccountNumberPaginated(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TransactionResponse> transactions = transactionService.getTransactionsByAccountNumberPaginated(accountNumber, pageable);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Listar transacciones por ID de cuenta con paginación")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/paginated/account/id/{accountId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByAccountIdPaginated(
            @PathVariable String accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TransactionResponse> transactions = transactionService.getTransactionsByAccountIdPaginated(accountId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Listar transacciones por rango de fechas con paginación")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/paginated/date-range")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TransactionResponse> transactions = transactionService.getTransactionsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Listar transacciones por número de cuenta y rango de fechas con paginación")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/paginated/account/number/{accountNumber}/date-range")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByAccountNumberAndDateRange(
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TransactionResponse> transactions = transactionService.getTransactionsByAccountNumberAndDateRange(
                accountNumber, startDate, endDate, pageable);
        return ResponseEntity.ok(transactions);
    }
}
