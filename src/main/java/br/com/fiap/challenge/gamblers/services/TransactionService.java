package br.com.fiap.challenge.gamblers.services;

import br.com.fiap.challenge.gamblers.entities.Transaction;
import br.com.fiap.challenge.gamblers.entities.User;
import br.com.fiap.challenge.gamblers.entities.dtos.CreateTransactionDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.TransactionDTO;
import br.com.fiap.challenge.gamblers.exception.NotFoundException;
import br.com.fiap.challenge.gamblers.repositories.TransactionRepository;
import br.com.fiap.challenge.gamblers.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionDTO create(CreateTransactionDTO dto) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new NotFoundException("User not found"));

        Transaction tx = Transaction.builder()
                .user(user)
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .type(dto.getType())
                .createdAt(LocalDateTime.now())
                .build();

        tx = transactionRepository.save(tx);
        return toDTO(tx);
    }

    public List<TransactionDTO> findByUser(UUID userId) {
        return transactionRepository.findByUserId(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TransactionDTO> findAll() {
        return transactionRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public TransactionDTO findById(UUID id) {
        Transaction tx = transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction not found"));
        return toDTO(tx);
    }

    public void delete(UUID id) {
        if (!transactionRepository.existsById(id)) throw new NotFoundException("Transaction not found");
        transactionRepository.deleteById(id);
    }

    public TransactionDTO update(UUID id, CreateTransactionDTO dto) {
        Transaction tx = transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction not found"));
        // update fields
        tx.setAmount(dto.getAmount());
        tx.setDescription(dto.getDescription());
        tx.setType(dto.getType());
        tx = transactionRepository.save(tx);
        return toDTO(tx);
    }

    private TransactionDTO toDTO(Transaction tx) {
        return TransactionDTO.builder()
                .id(tx.getId())
                .userId(tx.getUser().getId())
                .amount(tx.getAmount())
                .description(tx.getDescription())
                .type(tx.getType())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
