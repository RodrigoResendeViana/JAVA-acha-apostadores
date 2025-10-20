package br.com.fiap.challenge.gamblers.interfaces;

import br.com.fiap.challenge.gamblers.entities.dtos.CreateTransactionDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.TransactionDTO;

import java.util.List;
import java.util.UUID;

public interface ITransactionService {
    TransactionDTO create(CreateTransactionDTO dto);
    List<TransactionDTO> findByUser(UUID userId);
    List<TransactionDTO> findAll(String description, String type);
    TransactionDTO findById(UUID id);
    void delete(UUID id);
    TransactionDTO update(UUID id, CreateTransactionDTO dto);
}
