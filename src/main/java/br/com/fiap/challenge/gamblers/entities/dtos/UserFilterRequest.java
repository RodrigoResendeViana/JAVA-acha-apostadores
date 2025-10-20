package br.com.fiap.challenge.gamblers.entities.dtos;

import lombok.Data;

@Data
public class UserFilterRequest {
    private String name;
    private String email;
}
