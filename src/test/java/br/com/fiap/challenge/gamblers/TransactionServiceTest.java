package br.com.fiap.challenge.gamblers;

import br.com.fiap.challenge.gamblers.entities.Transaction;
import br.com.fiap.challenge.gamblers.entities.TransactionType;
import br.com.fiap.challenge.gamblers.entities.User;
import br.com.fiap.challenge.gamblers.entities.dtos.CreateTransactionDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.TransactionDTO;
import br.com.fiap.challenge.gamblers.exception.NotFoundException;
import br.com.fiap.challenge.gamblers.repositories.TransactionRepository;
import br.com.fiap.challenge.gamblers.repositories.UserRepository;
import br.com.fiap.challenge.gamblers.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Transaction transaction;
    private CreateTransactionDTO createTransactionDTO;
    private UUID userId;
    private UUID transactionId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john@example.com")
                .passwordHash("hashedPassword")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .build();
        transaction = Transaction.builder()
                .id(transactionId)
                .user(user)
                .amount(BigDecimal.valueOf(100.0))
                .description("Test transaction")
                .type(TransactionType.DEPOSIT)
                .createdAt(LocalDateTime.now())
                .build();
        createTransactionDTO = new CreateTransactionDTO(userId, BigDecimal.valueOf(100.0), "Test transaction", TransactionType.DEPOSIT);
    }

    @Test
    void testCreateTransaction() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = transactionService.create(createTransactionDTO);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100.0), result.getAmount());
        assertEquals("Test transaction", result.getDescription());
        verify(userRepository).findById(userId);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService.create(createTransactionDTO));
        verify(userRepository).findById(userId);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testFindByUser() {
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction));

        List<TransactionDTO> result = transactionService.findByUser(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(transactionId, result.get(0).getId());
        verify(transactionRepository).findByUserId(userId);
    }

    @Test
    void testFindById() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        TransactionDTO result = transactionService.findById(transactionId);

        assertNotNull(result);
        assertEquals(transactionId, result.getId());
        assertEquals(BigDecimal.valueOf(100.0), result.getAmount());
        verify(transactionRepository).findById(transactionId);
    }

    @Test
    void testFindById_NotFound() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService.findById(transactionId));
        verify(transactionRepository).findById(transactionId);
    }

    @Test
    void testDelete() {
        when(transactionRepository.existsById(transactionId)).thenReturn(true);

        transactionService.delete(transactionId);

        verify(transactionRepository).existsById(transactionId);
        verify(transactionRepository).deleteById(transactionId);
    }

    @Test
    void testDelete_NotFound() {
        when(transactionRepository.existsById(transactionId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> transactionService.delete(transactionId));
        verify(transactionRepository).existsById(transactionId);
        verify(transactionRepository, never()).deleteById(transactionId);
    }

    @Test
    void testUpdate() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = transactionService.update(transactionId, createTransactionDTO);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100.0), result.getAmount());
        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository).save(any(Transaction.class));
    }
}