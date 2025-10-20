package br.com.fiap.challenge.gamblers.entities.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Credenciais para autenticação", example = "{ \"email\": \"joao@example.com\", \"password\": \"senhaSegura123\" }")
public class LoginRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
