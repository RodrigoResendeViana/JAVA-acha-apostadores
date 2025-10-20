package br.com.fiap.challenge.gamblers;

import br.com.fiap.challenge.gamblers.entities.Transaction;
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

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID transactionId;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        userId = user.getId();

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(BigDecimal.valueOf(100.0));
        transaction.setDescription("Test transaction");
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction = transactionRepository.save(transaction);
        transactionId = transaction.getId();
    }

    @Test
    void testCreateTransaction() throws Exception {
        CreateTransactionDTO dto = new CreateTransactionDTO(userId, BigDecimal.valueOf(200.0), "New transaction", TransactionType.WITHDRAWAL);

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

        mockMvc.perform(put("/api/transactions/{id}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150.0))
                .andExpect(jsonPath("$.description").value("Updated transaction"));
    }

    @Test
    void testDeleteTransaction() throws Exception {
        mockMvc.perform(delete("/api/transactions/{id}", transactionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/transactions/{id}", transactionId))
                .andExpect(status().isNotFound());
    }
}