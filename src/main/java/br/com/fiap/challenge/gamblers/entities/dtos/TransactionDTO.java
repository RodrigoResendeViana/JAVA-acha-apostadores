package br.com.fiap.challenge.gamblers.entities.dtos;

import br.com.fiap.challenge.gamblers.entities.TransactionType;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Representação de uma transação retornada pela API")
public class TransactionDTO {
    private UUID id;
    private UUID userId;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
    private LocalDateTime createdAt;
}
