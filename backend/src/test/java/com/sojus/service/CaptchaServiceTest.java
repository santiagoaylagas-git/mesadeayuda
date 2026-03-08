package com.sojus.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para CaptchaService.
 * Cubre: captcha deshabilitado, token nulo/vacío, y manejo de errores.
 */
@DisplayName("CaptchaService — Tests Unitarios")
class CaptchaServiceTest {

    @Nested
    @DisplayName("reCAPTCHA deshabilitado")
    class CaptchaDeshabilitado {

        @Test
        @DisplayName("verify retorna true cuando reCAPTCHA está desactivado")
        void verify_deshabilitado_retornaTrue() {
            CaptchaService service = new CaptchaService(new RestTemplate(), "", false);
            assertThat(service.verify(null)).isTrue();
        }

        @Test
        @DisplayName("verify retorna true con token vacío cuando está desactivado")
        void verify_deshabilitado_tokenVacio_retornaTrue() {
            CaptchaService service = new CaptchaService(new RestTemplate(), "", false);
            assertThat(service.verify("")).isTrue();
        }

        @Test
        @DisplayName("verify retorna true con cualquier token cuando está desactivado")
        void verify_deshabilitado_cualquierToken_retornaTrue() {
            CaptchaService service = new CaptchaService(new RestTemplate(), "", false);
            assertThat(service.verify("cualquier-token")).isTrue();
        }
    }

    @Nested
    @DisplayName("reCAPTCHA habilitado")
    class CaptchaHabilitado {

        @Test
        @DisplayName("verify retorna false con token null")
        void verify_habilitado_tokenNull_retornaFalse() {
            CaptchaService service = new CaptchaService(new RestTemplate(), "fake-secret", true);
            assertThat(service.verify(null)).isFalse();
        }

        @Test
        @DisplayName("verify retorna false con token vacío")
        void verify_habilitado_tokenVacio_retornaFalse() {
            CaptchaService service = new CaptchaService(new RestTemplate(), "fake-secret", true);
            assertThat(service.verify("")).isFalse();
        }

        @Test
        @DisplayName("verify retorna false con token en blanco")
        void verify_habilitado_tokenBlanco_retornaFalse() {
            CaptchaService service = new CaptchaService(new RestTemplate(), "fake-secret", true);
            assertThat(service.verify("   ")).isFalse();
        }

        @Test
        @DisplayName("verify retorna false cuando Google API falla (secretKey inválida)")
        void verify_habilitado_apiError_retornaFalse() {
            // Con un secretKey inválido, la API de Google retorna success=false
            CaptchaService service = new CaptchaService(new RestTemplate(), "invalid-secret", true);
            assertThat(service.verify("fake-token")).isFalse();
        }
    }
}
