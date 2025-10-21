package br.com.fiap.challenge.gamblers;

import br.com.fiap.challenge.gamblers.controllers.UserController;
import br.com.fiap.challenge.gamblers.interfaces.IUserService;
import br.com.fiap.challenge.gamblers.entities.dtos.UserDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.CreateUserDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.UpdateUserDTO;
import br.com.fiap.challenge.gamblers.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.List;

import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        UserDTO userDTO = UserDTO.builder()
                .id(userId)
                .name("Test User")
                .email("test@example.com")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .build();
        when(userService.findById(userId)).thenReturn(userDTO);
        when(userService.findAll(anyString(), anyString())).thenReturn(List.of(userDTO));
    }

    @Test
    void testCreateUser() throws Exception {
        CreateUserDTO dto = new CreateUserDTO("New User", "new@example.com", "password123", false, true);
        UserDTO responseDTO = UserDTO.builder()
                .id(UUID.randomUUID())
                .name("New User")
                .email("new@example.com")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .build();
        when(userService.create(any(CreateUserDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        when(userService.findById(randomId)).thenThrow(new NotFoundException("User not found"));
        
        mockMvc.perform(get("/api/users/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testUpdateUser() throws Exception {
        UpdateUserDTO dto = new UpdateUserDTO("Updated User", "updated@example.com", false);
        UserDTO updatedDTO = UserDTO.builder()
                .id(userId)
                .name("Updated User")
                .email("updated@example.com")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .build();
        when(userService.update(eq(userId), any(UpdateUserDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService).delete(userId);
    }
}