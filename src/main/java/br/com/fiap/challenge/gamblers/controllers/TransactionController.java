package br.com.fiap.challenge.gamblers.controllers;

import br.com.fiap.challenge.gamblers.entities.dtos.CreateTransactionDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.TransactionDTO;
import br.com.fiap.challenge.gamblers.services.TransactionService;
import br.com.fiap.challenge.gamblers.entities.dtos.TransactionFilterRequest;
import br.com.fiap.challenge.gamblers.interfaces.ITransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Operações relacionadas a transações")
public class TransactionController {
    private final ITransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar transação", description = "Cria uma nova transação vinculada a um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação criada", content = @Content(schema = @Schema(implementation = TransactionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public TransactionDTO create(@Valid @RequestBody CreateTransactionDTO dto) {
        return transactionService.create(dto);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar transações de um usuário", description = "Retorna todas as transações de um usuário específico")
    public List<TransactionDTO> findByUser(@PathVariable UUID userId) {
        return transactionService.findByUser(userId);
    }

    @GetMapping
    @Operation(summary = "Listar transações", description = "Retorna a lista de transações, filtrável por descrição e tipo")
    public List<TransactionDTO> findAll(@org.springframework.web.bind.annotation.ModelAttribute TransactionFilterRequest filter) {
        return transactionService.findAll(filter.getDescription(), filter.getType());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar transação por id", description = "Retorna uma transação pelo seu identificador")
    public TransactionDTO findById(@PathVariable UUID id) {
        return transactionService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover transação", description = "Remove uma transação pelo seu id")
    public void delete(@PathVariable UUID id) {
        transactionService.delete(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar transação", description = "Atualiza os dados de uma transação existente")
    public TransactionDTO update(@PathVariable UUID id, @Valid @RequestBody CreateTransactionDTO dto) {
        return transactionService.update(id, dto);
    }
}
