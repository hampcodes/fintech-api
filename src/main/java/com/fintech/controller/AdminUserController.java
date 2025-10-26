package com.fintech.controller;

import com.fintech.dto.request.UpdateUserRoleRequest;
import com.fintech.dto.response.UserResponse;
import com.fintech.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - Users", description = "API de administración de usuarios (solo ADMIN)")
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "Listar todos los usuarios")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsersAdmin();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        UserResponse user = userService.getUserByIdAdmin(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Activar usuario")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable String id) {
        UserResponse user = userService.activateUserAdmin(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Desactivar usuario")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable String id) {
        UserResponse user = userService.deactivateUserAdmin(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Cambiar rol de usuario (USER <-> ADMIN)")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        UserResponse user = userService.updateUserRoleAdmin(id, request);
        return ResponseEntity.ok(user);
    }
}
