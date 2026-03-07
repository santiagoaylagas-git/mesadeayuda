package com.sojus.service;

import com.sojus.domain.entity.Juzgado;
import com.sojus.domain.entity.User;
import com.sojus.domain.enums.RoleName;
import com.sojus.dto.UserCreateRequest;
import com.sojus.dto.UserResponse;
import com.sojus.dto.UserUpdateRequest;
import com.sojus.exception.BusinessRuleException;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.JuzgadoRepository;
import com.sojus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio de gestión de usuarios del sistema.
 * Maneja creación con encriptación BCrypt, actualización, soft-delete,
 * y consultas por rol.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class UserService {

    private final UserRepository userRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ---- Métodos internos (devuelven entidades) ----

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAllByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .filter(u -> !u.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    @Transactional(readOnly = true)
    public List<User> findByRole(RoleName role) {
        return userRepository.findAllByRoleAndDeletedFalse(role);
    }

    /**
     * Crea un usuario a partir de un DTO de request.
     * Encripta la contraseña y valida unicidad del username.
     */
    @Transactional
    public UserResponse createFromRequest(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessRuleException("El nombre de usuario ya existe");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(RoleName.valueOf(request.getRole()))
                .build();

        if (request.getJuzgadoId() != null) {
            Juzgado juzgado = juzgadoRepository.findById(request.getJuzgadoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Juzgado", request.getJuzgadoId()));
            user.setJuzgado(juzgado);
        }

        User saved = userRepository.save(user);
        log.info("Usuario creado: {} (rol: {})", saved.getUsername(), saved.getRole());
        return toResponse(saved);
    }

    /**
     * Actualiza un usuario existente a partir de un DTO de request.
     * La contraseña solo se actualiza si se proporciona (no vacía).
     */
    @Transactional
    public UserResponse updateFromRequest(Long id, UserUpdateRequest request) {
        User existing = findById(id);
        existing.setFullName(request.getFullName());
        existing.setEmail(request.getEmail());
        existing.setRole(RoleName.valueOf(request.getRole()));
        if (request.getActive() != null) {
            existing.setActive(request.getActive());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        User saved = userRepository.save(existing);
        log.info("Usuario actualizado: {} (ID: {})", saved.getUsername(), saved.getId());
        return toResponse(saved);
    }

    /**
     * Soft-delete: marca al usuario como eliminado y desactivado.
     */
    @Transactional
    public void softDelete(Long id) {
        User user = findById(id);
        user.setDeleted(true);
        user.setActive(false);
        userRepository.save(user);
        log.info("Usuario eliminado (soft): {} (ID: {})", user.getUsername(), id);
    }

    // ---- Métodos DTO (para controllers) ----

    @Transactional(readOnly = true)
    public List<UserResponse> findAllAsDto() {
        return findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findByIdAsDto(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findByRoleAsDto(RoleName role) {
        return findByRole(role).stream().map(this::toResponse).toList();
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole().name())
                .juzgadoNombre(u.getJuzgado() != null ? u.getJuzgado().getNombre() : null)
                .active(u.getActive())
                .createdAt(u.getCreatedAt() != null ? u.getCreatedAt().format(FMT) : null)
                .build();
    }
}
