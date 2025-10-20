package br.com.fiap.challenge.gamblers.entities.dtos;

import br.com.fiap.challenge.gamblers.entities.TransactionType;
import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados necessários para criar uma transação", example = "{ \"userId\": \"00000000-0000-0000-0000-000000000000\", \"amount\": 100.5, \"description\": \"Aposta\", \"type\": " +
    "\"DEBIT\" }")
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
