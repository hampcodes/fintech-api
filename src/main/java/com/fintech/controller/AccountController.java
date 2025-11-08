package com.fintech.controller;

import com.fintech.dto.request.AccountRequest;
import com.fintech.dto.response.AccountResponse;
import com.fintech.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Accounts", description = "API de gestión de cuentas bancarias")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Crear nueva cuenta bancaria")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener cuenta por ID")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable String id) {
        AccountResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener cuenta por número")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(@PathVariable String accountNumber) {
        AccountResponse response = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todas las cuentas del usuario autenticado")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Listar cuentas activas del usuario autenticado")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/active")
    public ResponseEntity<List<AccountResponse>> getActiveAccounts() {
        List<AccountResponse> accounts = accountService.getActiveAccounts();
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Desactivar cuenta")
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<AccountResponse> deactivateAccount(@PathVariable String id) {
        AccountResponse response = accountService.deactivateAccount(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activar cuenta")
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<AccountResponse> activateAccount(@PathVariable String id) {
        AccountResponse response = accountService.activateAccount(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar saldo de cuenta")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/number/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable String accountNumber) {
        BigDecimal balance = accountService.getAccountBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }

    // ==================== PAGINATED ENDPOINTS ====================

    @Operation(summary = "Listar cuentas con paginación")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/paginated")
    public ResponseEntity<Page<AccountResponse>> getAllAccountsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AccountResponse> accounts = accountService.getAllAccountsPaginated(pageable);
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Listar cuentas activas con paginación")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/paginated/active")
    public ResponseEntity<Page<AccountResponse>> getActiveAccountsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AccountResponse> accounts = accountService.getActiveAccountsPaginated(pageable);
        return ResponseEntity.ok(accounts);
    }
}