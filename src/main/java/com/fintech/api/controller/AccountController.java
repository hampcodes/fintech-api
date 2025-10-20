package com.fintech.api.controller;

import com.fintech.api.dto.request.AccountRequest;
import com.fintech.api.dto.response.AccountResponse;
import com.fintech.api.dto.response.ErrorResponse;
import com.fintech.api.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Accounts", description = "API de gestión de cuentas bancarias")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Crear nueva cuenta bancaria",
            description = "Crea una nueva cuenta bancaria con los datos proporcionados. " +
                    "El número de cuenta debe ser único y tener entre 10-20 dígitos. " +
                    "El email debe tener formato válido. El saldo inicial debe ser >= 0."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cuenta creada exitosamente",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos (email inválido, saldo negativo, etc.)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Número de cuenta duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la cuenta a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AccountRequest.class))
            )
            @Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Obtener cuenta por ID",
            description = "Recupera la información completa de una cuenta bancaria por su ID único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuenta encontrada",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cuenta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
            @Parameter(description = "ID único de la cuenta", example = "acc-12345", required = true)
            @PathVariable String id) {
        AccountResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtener cuenta por número",
            description = "Recupera la información completa de una cuenta bancaria por su número de cuenta"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuenta encontrada",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cuenta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @Parameter(description = "Número de cuenta (10-20 dígitos)", example = "1234567890", required = true)
            @PathVariable String accountNumber) {
        AccountResponse response = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar todas las cuentas",
            description = "Obtiene una lista de todas las cuentas bancarias registradas en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de cuentas obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @Operation(
            summary = "Listar cuentas activas",
            description = "Obtiene una lista de todas las cuentas bancarias que están en estado activo"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de cuentas activas obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            )
    })
    @GetMapping("/active")
    public ResponseEntity<List<AccountResponse>> getActiveAccounts() {
        List<AccountResponse> accounts = accountService.getActiveAccounts();
        return ResponseEntity.ok(accounts);
    }

    @Operation(
            summary = "Desactivar cuenta",
            description = "Cambia el estado de una cuenta bancaria a inactivo. " +
                    "Las cuentas inactivas no pueden realizar transacciones."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuenta desactivada exitosamente",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cuenta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<AccountResponse> deactivateAccount(
            @Parameter(description = "ID de la cuenta a desactivar", required = true)
            @PathVariable String id) {
        AccountResponse response = accountService.deactivateAccount(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Activar cuenta",
            description = "Cambia el estado de una cuenta bancaria inactiva a activo. " +
                    "Las cuentas activas pueden realizar transacciones normalmente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuenta activada exitosamente",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cuenta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<AccountResponse> activateAccount(
            @Parameter(description = "ID de la cuenta a activar", required = true)
            @PathVariable String id) {
        AccountResponse response = accountService.activateAccount(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Consultar saldo de cuenta",
            description = "Obtiene el saldo actual de una cuenta bancaria específica"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Saldo obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cuenta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/number/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(
            @Parameter(description = "Número de cuenta", example = "1234567890", required = true)
            @PathVariable String accountNumber) {
        BigDecimal balance = accountService.getAccountBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }
}
