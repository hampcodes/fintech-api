package com.fintech.controller;

import com.fintech.dto.request.CustomerRequest;
import com.fintech.dto.response.CustomerResponse;
import com.fintech.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customer", description = "API de gestión de perfil de cliente")
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Obtener mi perfil de cliente")
    @GetMapping("/profile")
    public ResponseEntity<CustomerResponse> getMyProfile() {
        CustomerResponse response = customerService.getMyProfile();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar mi perfil de cliente")
    @PutMapping("/profile")
    public ResponseEntity<CustomerResponse> updateMyProfile(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.updateMyProfile(request);
        return ResponseEntity.ok(response);
    }
}
