package br.com.fiap.challenge.gamblers.services;

import br.com.fiap.challenge.gamblers.entities.User;
import br.com.fiap.challenge.gamblers.entities.dtos.CreateUserDTO;
import br.com.fiap.challenge.gamblers.entities.dtos.UserDTO;
import br.com.fiap.challenge.gamblers.exception.NotFoundException;
import br.com.fiap.challenge.gamblers.repositories.UserRepository;
import br.com.fiap.challenge.gamblers.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserDTO create(CreateUserDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .admin(dto.isAdmin())
                .createdAt(now)
                .consentGiven(dto.isConsentGiven())
                .consentAt(dto.isConsentGiven() ? now : null)
                .build();

        user = userRepository.save(user);

        return toDTO(user);
    }

    public UserDTO findById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return toDTO(user);
    }

    public java.util.List<UserDTO> findAll() {
        return findAll(null, null);
    }

    @Override
    public java.util.List<UserDTO> findAll(String name, String email) {
        return userRepository.findAll().stream()
                .filter(u -> name == null || u.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(u -> email == null || u.getEmail().toLowerCase().contains(email.toLowerCase()))
                .map(this::toDTO)
                .toList();
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

    public UserDTO setConsent(UUID id, boolean consent) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.setConsentGiven(consent);
        user.setConsentAt(consent ? LocalDateTime.now() : null);
        user = userRepository.save(user);
        log.info("Consent recorded for user {}: {} at {}", id, consent, user.getConsentAt());
        return toDTO(user);
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .admin(user.isAdmin())
                .createdAt(user.getCreatedAt())
                .consentGiven(user.isConsentGiven())
                .consentAt(user.getConsentAt())
                .build();
    }
}
