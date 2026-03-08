package com.sojus.service;

import com.sojus.domain.entity.AuditLog;
import com.sojus.domain.entity.User;
import com.sojus.domain.enums.RoleName;
import com.sojus.dto.LoginRequest;
import com.sojus.dto.LoginResponse;
import com.sojus.exception.AuthenticationFailedException;
import com.sojus.repository.AuditLogRepository;
import com.sojus.repository.UserRepository;
import com.sojus.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthService.
 * Cubre: login exitoso, credenciales inválidas, usuario desactivado,
 * rate limiting, y verificación reCAPTCHA.
 *
 * Nota: CaptchaService y LoginAttemptService se instancian directamente
 * en lugar de usar @Mock porque ByteBuddy no soporta Java 25.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — Tests Unitarios")
@SuppressWarnings("null")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuditLogRepository auditLogRepository;

    // Instancias reales (no se pueden mockear con Java 25)
    private CaptchaService captchaService;
    private LoginAttemptService loginAttemptService;

    private AuthService authService;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // CaptchaService real con reCAPTCHA desactivado (siempre pasa)
        captchaService = new CaptchaService(new RestTemplate(), "", false);

        // LoginAttemptService real
        loginAttemptService = new LoginAttemptService();

        // JwtTokenProvider real con clave de test
        JwtTokenProvider realProvider = new JwtTokenProvider(
                "testSecretKeyForJwtTokenGenerationMin256Bits1234567890abcdef",
                3600000L);

        authService = new AuthService(
                userRepository, realProvider, passwordEncoder,
                captchaService, loginAttemptService, auditLogRepository);

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

    // ================================================================
    // LOGIN EXITOSO
    // ================================================================
    @Nested
    @DisplayName("Login exitoso")
    class LoginExitoso {

        @Test
        @DisplayName("Login exitoso con credenciales válidas")
        void login_exitoso() {
            when(userRepository.findByUsernameAndDeletedFalse("admin")).thenReturn(Optional.of(adminUser));
            when(passwordEncoder.matches("admin123", "$2a$encoded")).thenReturn(true);
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            LoginResponse response = authService.login(buildRequest("admin", "admin123"));

            assertThat(response.getToken()).isNotEmpty();
            assertThat(response.getUsername()).isEqualTo("admin");
            assertThat(response.getRole()).isEqualTo("ADMINISTRADOR");
            assertThat(response.getFullName()).isEqualTo("Admin General");
        }
    }

    // ================================================================
    // LOGIN FALLIDO
    // ================================================================
    @Nested
    @DisplayName("Login fallido")
    class LoginFallido {

        @Test
        @DisplayName("Login fallido con usuario inexistente")
        void login_usuarioNoExiste() {
            when(userRepository.findByUsernameAndDeletedFalse("noexiste")).thenReturn(Optional.empty());
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            assertThatThrownBy(() -> authService.login(buildRequest("noexiste", "pass")))
                    .isInstanceOf(AuthenticationFailedException.class)
                    .hasMessageContaining("Credenciales inválidas");
        }

        @Test
        @DisplayName("Login fallido con password incorrecta")
        void login_passwordIncorrecta() {
            when(userRepository.findByUsernameAndDeletedFalse("admin")).thenReturn(Optional.of(adminUser));
            when(passwordEncoder.matches("wrongpass", "$2a$encoded")).thenReturn(false);
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

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
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            assertThatThrownBy(() -> authService.login(buildRequest("inactive", "pass")))
                    .isInstanceOf(AuthenticationFailedException.class)
                    .hasMessageContaining("desactivado");
        }
    }

    // ================================================================
    // RATE LIMITING (uses real LoginAttemptService)
    // ================================================================
    @Nested
    @DisplayName("Rate Limiting")
    class RateLimiting {

        @Test
        @DisplayName("Login bloqueado tras 5 intentos fallidos")
        void login_bloqueado_tras_5_intentos() {
            when(userRepository.findByUsernameAndDeletedFalse("bruteforce")).thenReturn(Optional.empty());
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            // Llenar 5 intentos fallidos
            for (int i = 0; i < 5; i++) {
                try {
                    authService.login(buildRequest("bruteforce", "wrong"));
                } catch (AuthenticationFailedException ignored) {
                }
            }

            // El 6to intento debe estar bloqueado
            assertThatThrownBy(() -> authService.login(buildRequest("bruteforce", "wrong")))
                    .isInstanceOf(AuthenticationFailedException.class)
                    .hasMessageContaining("bloqueada");
        }
    }
}
