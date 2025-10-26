package com.fintech.controller;

import com.fintech.dto.request.UpdateKycRequest;
import com.fintech.dto.response.CustomerResponse;
import com.fintech.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - Customers", description = "API de administración de clientes (solo ADMIN)")
@RestController
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Listar todos los clientes")
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getAllCustomersAdmin();
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Obtener cliente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable String id) {
        CustomerResponse customer = customerService.getCustomerByIdAdmin(id);
        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Actualizar estado KYC del cliente")
    @PatchMapping("/{id}/kyc")
    public ResponseEntity<CustomerResponse> updateKycStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateKycRequest request) {
        CustomerResponse customer = customerService.updateKycStatusAdmin(id, request);
        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Activar cliente")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<CustomerResponse> activateCustomer(@PathVariable String id) {
        CustomerResponse customer = customerService.activateCustomerAdmin(id);
        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Desactivar cliente")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CustomerResponse> deactivateCustomer(@PathVariable String id) {
        CustomerResponse customer = customerService.deactivateCustomerAdmin(id);
        return ResponseEntity.ok(customer);
    }
}
