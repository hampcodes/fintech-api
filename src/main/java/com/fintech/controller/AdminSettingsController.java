package com.fintech.controller;

import com.fintech.dto.request.SettingRequest;
import com.fintech.dto.response.SettingResponse;
import com.fintech.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - Settings", description = "API de configuración del sistema (solo ADMIN)")
@RestController
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSettingsController {

    private final SettingsService settingsService;

    @Operation(summary = "Crear nueva configuración")
    @PostMapping
    public ResponseEntity<SettingResponse> createSetting(@Valid @RequestBody SettingRequest request) {
        SettingResponse response = settingsService.createSetting(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todas las configuraciones")
    @GetMapping
    public ResponseEntity<List<SettingResponse>> getAllSettings() {
        List<SettingResponse> settings = settingsService.getAllSettings();
        return ResponseEntity.ok(settings);
    }

    @Operation(summary = "Obtener configuración por clave")
    @GetMapping("/{key}")
    public ResponseEntity<SettingResponse> getSettingByKey(@PathVariable String key) {
        SettingResponse setting = settingsService.getSettingByKey(key);
        return ResponseEntity.ok(setting);
    }

    @Operation(summary = "Actualizar configuración")
    @PutMapping("/{key}")
    public ResponseEntity<SettingResponse> updateSetting(
            @PathVariable String key,
            @Valid @RequestBody SettingRequest request) {
        SettingResponse response = settingsService.updateSetting(key, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar configuración")
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteSetting(@PathVariable String key) {
        settingsService.deleteSetting(key);
        return ResponseEntity.noContent().build();
    }
}
