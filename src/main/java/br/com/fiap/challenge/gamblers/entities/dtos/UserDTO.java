package br.com.fiap.challenge.gamblers.entities.dtos;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Representação de um usuário retornada pela API")
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private boolean admin;
    private LocalDateTime createdAt;
}
