package com.sojus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para LoginAttemptService.
 * Cubre: registro de intentos fallidos, bloqueo tras MAX_ATTEMPTS,
 * reset de intentos, expiración de bloqueo, y case-insensitivity.
 */
@DisplayName("LoginAttemptService — Tests Unitarios")
class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService();
    }

    @Nested
    @DisplayName("Registro de intentos fallidos")
    class IntentosFallidos {

        @Test
        @DisplayName("Un intento fallido no bloquea al usuario")
        void unIntentoNoBloquea() {
            service.registerFailedAttempt("usuario1");
            assertThat(service.isBlocked("usuario1")).isFalse();
        }

        @Test
        @DisplayName("4 intentos fallidos no bloquean al usuario")
        void cuatroIntentosNoBloquearn() {
            for (int i = 0; i < 4; i++) {
                service.registerFailedAttempt("usuario2");
            }
            assertThat(service.isBlocked("usuario2")).isFalse();
        }

        @Test
        @DisplayName("5 intentos fallidos bloquean al usuario")
        void cincoIntentosBloquearn() {
            for (int i = 0; i < 5; i++) {
                service.registerFailedAttempt("usuario3");
            }
            assertThat(service.isBlocked("usuario3")).isTrue();
        }

        @Test
        @DisplayName("Más de 5 intentos mantienen bloqueado al usuario")
        void masDecincoIntentosMantienenBloqueo() {
            for (int i = 0; i < 7; i++) {
                service.registerFailedAttempt("usuario4");
            }
            assertThat(service.isBlocked("usuario4")).isTrue();
        }
    }

    @Nested
    @DisplayName("Reset de intentos")
    class ResetIntentos {

        @Test
        @DisplayName("resetAttempts desbloquea al usuario")
        void resetDesbloquea() {
            for (int i = 0; i < 5; i++) {
                service.registerFailedAttempt("usuario5");
            }
            assertThat(service.isBlocked("usuario5")).isTrue();

            service.resetAttempts("usuario5");
            assertThat(service.isBlocked("usuario5")).isFalse();
        }

        @Test
        @DisplayName("resetAttempts de usuario sin intentos no lanza error")
        void resetSinIntentosNoFalla() {
            assertThatNoException().isThrownBy(() -> service.resetAttempts("noexiste"));
        }
    }

    @Nested
    @DisplayName("Minutos restantes de bloqueo")
    class MinutosRestantes {

        @Test
        @DisplayName("getRemainingLockMinutes retorna > 0 cuando está bloqueado")
        void minutosRestantesCuandoBloqueado() {
            for (int i = 0; i < 5; i++) {
                service.registerFailedAttempt("usuario6");
            }
            long minutes = service.getRemainingLockMinutes("usuario6");
            assertThat(minutes).isGreaterThan(0);
        }

        @Test
        @DisplayName("getRemainingLockMinutes retorna 0 para usuario sin intentos")
        void minutosRestantesSinIntentos() {
            assertThat(service.getRemainingLockMinutes("noexiste")).isEqualTo(0);
        }

        @Test
        @DisplayName("getRemainingLockMinutes retorna 0 para usuario no bloqueado")
        void minutosRestantesNoBloqueado() {
            service.registerFailedAttempt("usuario7");
            assertThat(service.getRemainingLockMinutes("usuario7")).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Case-insensitive")
    class CaseInsensitive {

        @Test
        @DisplayName("El bloqueo es case-insensitive")
        void bloqueoCaseInsensitive() {
            for (int i = 0; i < 5; i++) {
                service.registerFailedAttempt("Admin");
            }
            assertThat(service.isBlocked("admin")).isTrue();
            assertThat(service.isBlocked("ADMIN")).isTrue();
        }

        @Test
        @DisplayName("El reset es case-insensitive")
        void resetCaseInsensitive() {
            for (int i = 0; i < 5; i++) {
                service.registerFailedAttempt("Admin");
            }
            service.resetAttempts("ADMIN");
            assertThat(service.isBlocked("admin")).isFalse();
        }
    }

    @Nested
    @DisplayName("isBlocked sin intentos previos")
    class SinIntentosPrevios {

        @Test
        @DisplayName("isBlocked retorna false para usuario nuevo")
        void usuarioNuevoNoEstaBloqueado() {
            assertThat(service.isBlocked("nuevo")).isFalse();
        }
    }
}
