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

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operações relacionadas a usuários")
public class UserController {
    private final IUserService userService;

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
    public UserDTO findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna a lista de usuários, filtrável por nome e email")
    public List<UserDTO> findAll(@org.springframework.web.bind.annotation.ModelAttribute UserFilterRequest filter) {
        return userService.findAll(filter.getName(), filter.getEmail());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    public UserDTO update(@PathVariable UUID id, @Valid @RequestBody UpdateUserDTO dto) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover usuário", description = "Remove um usuário pelo seu id")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
