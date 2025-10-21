package br.com.fiap.challenge.gamblers.controllers;

import br.com.fiap.challenge.gamblers.entities.dtos.CreateUserDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.UserDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.UpdateUserDTO;
import br.com.fiap.challenge.gamblers.services.UserService;
import br.com.fiap.challenge.gamblers.entities.dtos.UserFilterRequest;
import br.com.fiap.challenge.gamblers.interfaces.IUserService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operações relacionadas a usuários")
public class UserController {
    private final IUserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado", content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public UserDTO create(@Valid @RequestBody CreateUserDTO dto) {
        return userService.create(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por id", description = "Retorna um usuário pelo seu identificador")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public UserDTO findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna a lista de usuários, filtrável por nome e email")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> findAll(@org.springframework.web.bind.annotation.ModelAttribute UserFilterRequest filter) {
        return userService.findAll(filter.getName(), filter.getEmail());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public UserDTO update(@PathVariable UUID id, @Valid @RequestBody UpdateUserDTO dto) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover usuário", description = "Remove um usuário pelo seu id")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }

    // New endpoint to register consent changes
    @PostMapping("/{id}/consent")
    @Operation(summary = "Registrar consentimento", description = "Registra ou remove o consentimento de um usuário")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public UserDTO setConsent(@PathVariable UUID id, @RequestBody ConsentRequest req) {
        log.info("Consent update: userId={} consent={}", id, req.isConsent());
        return userService.setConsent(id, req.isConsent());
    }

    // Simple DTO for consent endpoint
    public static class ConsentRequest {
        private boolean consent;

        public boolean isConsent() { return consent; }
        public void setConsent(boolean consent) { this.consent = consent; }
    }
}
