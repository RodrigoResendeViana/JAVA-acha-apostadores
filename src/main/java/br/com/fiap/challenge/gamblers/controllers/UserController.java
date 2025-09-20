package br.com.fiap.challenge.gamblers.controllers;

import br.com.fiap.challenge.gamblers.entities.dtos.CreateUserDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.UserDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.UpdateUserDTO;
import br.com.fiap.challenge.gamblers.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody CreateUserDTO dto) {
        return userService.create(dto);
    }

    @GetMapping("/{id}")
    public UserDTO findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable UUID id, @Valid @RequestBody UpdateUserDTO dto) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
