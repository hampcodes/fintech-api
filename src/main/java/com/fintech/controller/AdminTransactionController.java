package com.fintech.controller;

import com.fintech.dto.response.TransactionResponse;
import com.fintech.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - Transactions", description = "API de administración de transacciones (solo ADMIN)")
@RestController
@RequestMapping("/admin/transactions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Listar todas las transacciones de todos los usuarios")
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionService.getAllTransactionsAdmin();
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Listar transacciones por número de cuenta (admin)")
    @GetMapping("/account/number/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountNumber(@PathVariable String accountNumber) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Listar transacciones por ID de cuenta (admin)")
    @GetMapping("/account/id/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountId(@PathVariable String accountId) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }
}
