package br.com.fiap.challenge.gamblers.entities.dtos;

import br.com.fiap.challenge.gamblers.entities.TransactionType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionDTO {
    @NotNull
    private UUID userId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @Size(max = 255)
    private String description;

    @NotNull
    private TransactionType type;
}
