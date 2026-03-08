package com.sojus.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de protección contra fuerza bruta en login.
 * Bloquea un username tras MAX_ATTEMPTS intentos fallidos
 * durante LOCK_DURATION_MINUTES minutos.
 *
 * Implementación in-memory con ConcurrentHashMap (adecuada
 * para una única instancia; para cluster usar Redis).
 */
@Service
@Slf4j
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    private final ConcurrentHashMap<String, AttemptInfo> attemptsMap = new ConcurrentHashMap<>();

    /**
     * Verifica si un username está bloqueado por exceso de intentos.
     */
    public boolean isBlocked(String username) {
        AttemptInfo info = attemptsMap.get(username.toLowerCase());
        if (info == null) {
            return false;
        }

        // Si el bloqueo expiró, limpiar y permitir
        if (info.blockedUntil != null && LocalDateTime.now().isAfter(info.blockedUntil)) {
            attemptsMap.remove(username.toLowerCase());
            return false;
        }

        return info.attempts >= MAX_ATTEMPTS;
    }

    /**
     * Registra un intento fallido de login.
     * Si alcanza MAX_ATTEMPTS, bloquea por LOCK_DURATION_MINUTES.
     */
    public void registerFailedAttempt(String username) {
        String key = username.toLowerCase();
        attemptsMap.compute(key, (k, existing) -> {
            if (existing == null) {
                return new AttemptInfo(1, null);
            }
            int newAttempts = existing.attempts + 1;
            LocalDateTime blockedUntil = null;
            if (newAttempts >= MAX_ATTEMPTS) {
                blockedUntil = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
                log.warn("Usuario '{}' bloqueado por {} minutos tras {} intentos fallidos",
                        username, LOCK_DURATION_MINUTES, newAttempts);
            }
            return new AttemptInfo(newAttempts, blockedUntil);
        });
    }

    /**
     * Resetea los intentos fallidos tras un login exitoso.
     */
    public void resetAttempts(String username) {
        attemptsMap.remove(username.toLowerCase());
    }

    /**
     * Retorna los minutos restantes de bloqueo.
     * 
     * @return minutos restantes, o 0 si no está bloqueado
     */
    public long getRemainingLockMinutes(String username) {
        AttemptInfo info = attemptsMap.get(username.toLowerCase());
        if (info == null || info.blockedUntil == null) {
            return 0;
        }
        long remaining = java.time.Duration.between(LocalDateTime.now(), info.blockedUntil).toMinutes();
        return Math.max(0, remaining);
    }

    private static class AttemptInfo {
        final int attempts;
        final LocalDateTime blockedUntil;

        AttemptInfo(int attempts, LocalDateTime blockedUntil) {
            this.attempts = attempts;
            this.blockedUntil = blockedUntil;
        }
    }
}
