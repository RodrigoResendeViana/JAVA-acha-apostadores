package br.com.fiap.challenge.gamblers.controllers;

import br.com.fiap.challenge.gamblers.entities.dtos.CreateTransactionDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.TransactionDTO;
import br.com.fiap.challenge.gamblers.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO create(@Valid @RequestBody CreateTransactionDTO dto) {
        return transactionService.create(dto);
    }

    @GetMapping("/user/{userId}")
    public List<TransactionDTO> findByUser(@PathVariable UUID userId) {
        return transactionService.findByUser(userId);
    }

    @GetMapping
    public List<TransactionDTO> findAll() {
        return transactionService.findAll();
    }

    @GetMapping("/{id}")
    public TransactionDTO findById(@PathVariable UUID id) {
        return transactionService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        transactionService.delete(id);
    }

    @PutMapping("/{id}")
    public TransactionDTO update(@PathVariable UUID id, @Valid @RequestBody CreateTransactionDTO dto) {
        return transactionService.update(id, dto);
    }
}
