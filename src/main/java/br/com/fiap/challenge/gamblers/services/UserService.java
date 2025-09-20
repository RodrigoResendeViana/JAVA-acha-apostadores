package br.com.fiap.challenge.gamblers.services;

import br.com.fiap.challenge.gamblers.entities.User;
import br.com.fiap.challenge.gamblers.entities.dtos.CreateUserDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.UserDTO;
import br.com.fiap.challenge.gamblers.exception.NotFoundException;
import br.com.fiap.challenge.gamblers.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserDTO create(CreateUserDTO dto) {
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .admin(dto.isAdmin())
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        return toDTO(user);
    }

    public UserDTO findById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return toDTO(user);
    }

    public java.util.List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    public UserDTO update(UUID id, br.com.fiap.challenge.gamblers.entities.dtos.UpdateUserDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        user.setAdmin(dto.isAdmin());
        user = userRepository.save(user);
        return toDTO(user);
    }

    public void delete(UUID id) {
        if (!userRepository.existsById(id)) throw new NotFoundException("User not found");
        userRepository.deleteById(id);
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .admin(user.isAdmin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
