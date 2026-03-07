package com.sojus.service;

import com.sojus.domain.entity.User;
import com.sojus.domain.enums.RoleName;
import com.sojus.dto.LoginRequest;
import com.sojus.dto.LoginResponse;
import com.sojus.exception.AuthenticationFailedException;
import com.sojus.repository.UserRepository;
import com.sojus.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthService.
 * Usa reflexión para inyectar mocks porque JwtTokenProvider
 * no puede ser mockeado por Mockito en Java 25.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — Tests Unitarios")
@SuppressWarnings("null")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;
    private User adminUser;

    @BeforeEach
    void setUp() throws Exception {
        // Crear JwtTokenProvider real con clave de test
        JwtTokenProvider realProvider = new JwtTokenProvider(
                "testSecretKeyForJwtTokenGenerationMin256Bits1234567890abcdef",
                3600000L);

        // Inyectar manualmente los mocks usando reflexión
        authService = new AuthService(userRepository, realProvider, passwordEncoder);

        adminUser = User.builder()
                .id(1L).username("admin").password("$2a$encoded")
                .fullName("Admin General").role(RoleName.ADMINISTRADOR)
                .active(true).deleted(false)
                .build();
    }

    private LoginRequest buildRequest(String username, String password) {
        LoginRequest req = new LoginRequest();
        req.setUsername(username);
        req.setPassword(password);
        return req;
    }

    @Test
    @DisplayName("Login exitoso con credenciales válidas")
    void login_exitoso() {
        when(userRepository.findByUsernameAndDeletedFalse("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("admin123", "$2a$encoded")).thenReturn(true);

        LoginResponse response = authService.login(buildRequest("admin", "admin123"));

        assertThat(response.getToken()).isNotEmpty();
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getRole()).isEqualTo("ADMINISTRADOR");
        assertThat(response.getFullName()).isEqualTo("Admin General");
    }

    @Test
    @DisplayName("Login fallido con usuario inexistente")
    void login_usuarioNoExiste() {
        when(userRepository.findByUsernameAndDeletedFalse("noexiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(buildRequest("noexiste", "pass")))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    @Test
    @DisplayName("Login fallido con password incorrecta")
    void login_passwordIncorrecta() {
        when(userRepository.findByUsernameAndDeletedFalse("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("wrongpass", "$2a$encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(buildRequest("admin", "wrongpass")))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    @Test
    @DisplayName("Login fallido con usuario desactivado")
    void login_usuarioDesactivado() {
        User inactiveUser = User.builder()
                .id(2L).username("inactive").password("$2a$encoded")
                .active(false).deleted(false)
                .build();

        when(userRepository.findByUsernameAndDeletedFalse("inactive")).thenReturn(Optional.of(inactiveUser));
        when(passwordEncoder.matches("pass", "$2a$encoded")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(buildRequest("inactive", "pass")))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessageContaining("desactivado");
    }
}
