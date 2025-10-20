package br.com.fiap.challenge.gamblers.entities.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@Schema(description = "Resposta de login contendo o token JWT")
public class LoginResponse {
    private String token;
}
