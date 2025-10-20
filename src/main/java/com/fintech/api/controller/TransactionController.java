package com.fintech.api.controller;

import com.fintech.api.dto.request.TransactionRequest;
import com.fintech.api.dto.response.ErrorResponse;
import com.fintech.api.dto.response.TransactionResponse;
import com.fintech.api.service.TransactionService;
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

import java.util.List;

@Tag(name = "Transactions", description = "API de gestión de transacciones financieras")
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
            summary = "Crear nueva transacción",
            description = "Procesa una nueva transacción bancaria (depósito o retiro). " +
                    "El monto debe ser mayor a 0. " +
                    "Para retiros, el saldo debe ser suficiente. " +
                    "La cuenta debe estar activa para realizar transacciones."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transacción creada exitosamente",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos (monto <= 0, tipo inválido, etc.)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cuenta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Saldo insuficiente para retiro o cuenta inactiva",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la transacción a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransactionRequest.class))
            )
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Obtener transacción por ID",
            description = "Recupera la información completa de una transacción por su ID único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transacción encontrada",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transacción no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @Parameter(description = "ID único de la transacción", example = "txn-12345", required = true)
            @PathVariable String id) {
        TransactionResponse response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar todas las transacciones",
            description = "Obtiene una lista de todas las transacciones registradas en el sistema, " +
                    "ordenadas por fecha de creación (más recientes primero)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de transacciones obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @Operation(
            summary = "Listar transacciones por ID de cuenta",
            description = "Obtiene todas las transacciones de una cuenta específica por su ID, " +
                    "ordenadas por fecha de creación (más recientes primero)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de transacciones obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cuenta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountId(
            @Parameter(description = "ID de la cuenta", example = "acc-12345", required = true)
            @PathVariable String accountId) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    @Operation(
            summary = "Listar transacciones por número de cuenta",
            description = "Obtiene todas las transacciones de una cuenta específica por su número de cuenta, " +
                    "ordenadas por fecha de creación (más recientes primero)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de transacciones obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cuenta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/account/number/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountNumber(
            @Parameter(description = "Número de cuenta (10-20 dígitos)", example = "1234567890", required = true)
            @PathVariable String accountNumber) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);
        return ResponseEntity.ok(transactions);
    }
}
