package br.com.fiap.challenge.gamblers.entities.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private boolean admin;
    private LocalDateTime createdAt;
}
