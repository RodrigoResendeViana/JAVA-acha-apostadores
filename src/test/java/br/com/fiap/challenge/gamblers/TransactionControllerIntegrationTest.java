package br.com.fiap.challenge.gamblers;

import br.com.fiap.challenge.gamblers.controllers.TransactionController;
import br.com.fiap.challenge.gamblers.interfaces.ITransactionService;
import br.com.fiap.challenge.gamblers.entities.dtos.TransactionDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.CreateTransactionDTO;
import br.com.fiap.challenge.gamblers.entities.TransactionType;
import br.com.fiap.challenge.gamblers.exception.NotFoundException;
import br.com.fiap.challenge.gamblers.entities.TransactionType;
import br.com.fiap.challenge.gamblers.entities.User;
import br.com.fiap.challenge.gamblers.entities.dtos.CreateTransactionDTO;
import br.com.fiap.challenge.gamblers.repositories.TransactionRepository;
import br.com.fiap.challenge.gamblers.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.List;

@WebMvcTest(TransactionController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID transactionId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .id(transactionId)
                .userId(userId)
                .amount(BigDecimal.valueOf(100.0))
                .description("Test transaction")
                .type(TransactionType.DEPOSIT)
                .createdAt(LocalDateTime.now())
                .build();
        when(transactionService.findById(transactionId)).thenReturn(transactionDTO);
        when(transactionService.findByUser(userId)).thenReturn(List.of(transactionDTO));
        when(transactionService.findAll(null, null)).thenReturn(List.of(transactionDTO));
    }

    @Test
    void testCreateTransaction() throws Exception {
        CreateTransactionDTO dto = new CreateTransactionDTO(userId, BigDecimal.valueOf(200.0), "New transaction", TransactionType.WITHDRAWAL);
        TransactionDTO responseDTO = TransactionDTO.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .amount(BigDecimal.valueOf(200.0))
                .description("New transaction")
                .type(TransactionType.WITHDRAWAL)
                .createdAt(LocalDateTime.now())
                .build();
        when(transactionService.create(any(CreateTransactionDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(200.0))
                .andExpect(jsonPath("$.description").value("New transaction"));
    }

    @Test
    void testGetTransactionById() throws Exception {
        mockMvc.perform(get("/api/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    void testGetTransactionById_NotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        when(transactionService.findById(randomId)).thenThrow(new NotFoundException("Transaction not found"));
        
        mockMvc.perform(get("/api/transactions/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTransactionsByUser() throws Exception {
        mockMvc.perform(get("/api/transactions/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].amount").value(100.0));
    }

    @Test
    void testGetAllTransactions() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testUpdateTransaction() throws Exception {
        CreateTransactionDTO dto = new CreateTransactionDTO(userId, BigDecimal.valueOf(150.0), "Updated transaction", TransactionType.DEPOSIT);
        TransactionDTO updatedDTO = TransactionDTO.builder()
                .id(transactionId)
                .userId(userId)
                .amount(BigDecimal.valueOf(150.0))
                .description("Updated transaction")
                .type(TransactionType.DEPOSIT)
                .createdAt(LocalDateTime.now())
                .build();
        when(transactionService.update(eq(transactionId), any(CreateTransactionDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/transactions/{id}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150.0))
                .andExpect(jsonPath("$.description").value("Updated transaction"));
    }

    @Test
    void testDeleteTransaction() throws Exception {
        doNothing().when(transactionService).delete(transactionId);

        mockMvc.perform(delete("/api/transactions/{id}", transactionId))
                .andExpect(status().isNoContent());

        verify(transactionService).delete(transactionId);
    }
}