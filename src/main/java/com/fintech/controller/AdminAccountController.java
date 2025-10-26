package com.fintech.controller;

import com.fintech.dto.response.AccountResponse;
import com.fintech.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - Accounts", description = "API de administración de cuentas (solo ADMIN)")
@RestController
@RequestMapping("/admin/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountController {

    private final AccountService accountService;

    @Operation(summary = "Listar todas las cuentas de todos los usuarios")
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccountsAdmin();
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Obtener cualquier cuenta por ID")
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable String id) {
        AccountResponse response = accountService.getAccountByIdAdmin(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desactivar cualquier cuenta")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<AccountResponse> deactivateAccount(@PathVariable String id) {
        AccountResponse response = accountService.deactivateAccountAdmin(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activar cualquier cuenta")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<AccountResponse> activateAccount(@PathVariable String id) {
        AccountResponse response = accountService.activateAccountAdmin(id);
        return ResponseEntity.ok(response);
    }
}
