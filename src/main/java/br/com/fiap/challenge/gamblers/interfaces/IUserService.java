package br.com.fiap.challenge.gamblers.interfaces;

import br.com.fiap.challenge.gamblers.entities.dtos.CreateUserDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.UpdateUserDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.UserDTO;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    UserDTO create(CreateUserDTO dto);
    UserDTO findById(UUID id);
    List<UserDTO> findAll(String name, String email);
    UserDTO update(UUID id, UpdateUserDTO dto);
    void delete(UUID id);
}
