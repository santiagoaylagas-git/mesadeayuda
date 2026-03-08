package com.sojus.service;

import com.sojus.domain.entity.AuditLog;
import com.sojus.domain.entity.User;
import com.sojus.dto.LoginRequest;
import com.sojus.dto.LoginResponse;
import com.sojus.exception.AuthenticationFailedException;
import com.sojus.repository.AuditLogRepository;
import com.sojus.repository.UserRepository;
import com.sojus.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de autenticación con protecciones de seguridad:
 * - Google reCAPTCHA v2
 * - Rate limiting (bloqueo tras intentos fallidos)
 * - Auditoría de logins exitosos y fallidos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaService captchaService;
    private final LoginAttemptService loginAttemptService;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();

        // 1. Verificar rate limiting
        if (loginAttemptService.isBlocked(username)) {
            long minutes = loginAttemptService.getRemainingLockMinutes(username);
            log.warn("Login bloqueado para '{}' — quedan {} minutos", username, minutes);
            throw new AuthenticationFailedException(
                    "Cuenta bloqueada por exceso de intentos. Intente nuevamente en " + minutes + " minutos");
        }

        // 2. Verificar reCAPTCHA
        if (!captchaService.verify(request.getCaptchaToken())) {
            log.warn("reCAPTCHA inválido para usuario '{}'", username);
            throw new AuthenticationFailedException("Verificación reCAPTCHA inválida");
        }

        // 3. Autenticar
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> {
                    loginAttemptService.registerFailedAttempt(username);
                    auditLogin(username, false, "Usuario no encontrado");
                    return new AuthenticationFailedException("Credenciales inválidas");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            loginAttemptService.registerFailedAttempt(username);
            auditLogin(username, false, "Contraseña incorrecta");
            throw new AuthenticationFailedException("Credenciales inválidas");
        }

        if (!user.getActive()) {
            auditLogin(username, false, "Usuario desactivado");
            throw new AuthenticationFailedException("Usuario desactivado");
        }

        // 4. Login exitoso — resetear intentos y registrar auditoría
        loginAttemptService.resetAttempts(username);
        auditLogin(username, true, "Login exitoso (rol: " + user.getRole().name() + ")");

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole().name());

        log.info("Login exitoso para '{}' (rol: {})", username, user.getRole());

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    @SuppressWarnings("null")
    private void auditLogin(String username, boolean success, String detail) {
        try {
            auditLogRepository.save(AuditLog.builder()
                    .entityName("Auth")
                    .entityId(0L)
                    .action(success ? "LOGIN_OK" : "LOGIN_FAIL")
                    .username(username)
                    .newValue(detail)
                    .build());
        } catch (Exception ex) {
            log.error("Error al registrar auditoría de login: {}", ex.getMessage());
        }
    }
}
