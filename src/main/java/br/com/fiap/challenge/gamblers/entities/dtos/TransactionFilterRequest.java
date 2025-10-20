package br.com.fiap.challenge.gamblers.entities.dtos;

import lombok.Data;

@Data
public class TransactionFilterRequest {
    private String description;
    private String type;
}
