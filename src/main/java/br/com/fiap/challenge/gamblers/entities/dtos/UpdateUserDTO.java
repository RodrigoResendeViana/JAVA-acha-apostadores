package br.com.fiap.challenge.gamblers.entities.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserDTO {
    @Size(max = 100)
    private String name;

    @Email
    private String email;

    private boolean admin;
}
