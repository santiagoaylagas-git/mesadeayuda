package com.sojus.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Servicio de verificación de Google reCAPTCHA v2.
 * Valida el token enviado por el frontend contra la API de Google.
 * Se puede desactivar con la propiedad app.recaptcha.enabled=false
 * para entornos de desarrollo y testing.
 */
@Service
@Slf4j
public class CaptchaService {

    private static final String GOOGLE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private final RestTemplate restTemplate;
    private final String secretKey;
    private final boolean enabled;

    public CaptchaService(
            RestTemplate restTemplate,
            @Value("${app.recaptcha.secret-key:}") String secretKey,
            @Value("${app.recaptcha.enabled:false}") boolean enabled) {
        this.restTemplate = restTemplate;
        this.secretKey = secretKey;
        this.enabled = enabled;
    }

    /**
     * Verifica el token reCAPTCHA con Google.
     * 
     * @param captchaToken token enviado por el frontend
     * @return true si el captcha es válido o si reCAPTCHA está desactivado
     */
    public boolean verify(String captchaToken) {
        if (!enabled) {
            log.debug("reCAPTCHA desactivado — omitiendo verificación");
            return true;
        }

        if (captchaToken == null || captchaToken.isBlank()) {
            log.warn("Token reCAPTCHA vacío o nulo");
            return false;
        }

        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey);
            params.add("response", captchaToken);

            @SuppressWarnings({ "rawtypes", "unchecked" })
            ResponseEntity<Map<String, Object>> response = (ResponseEntity) restTemplate.postForEntity(
                    GOOGLE_VERIFY_URL, params, Map.class);

            Map<String, Object> body = response.getBody();
            if (body == null) {
                log.error("Respuesta nula de Google reCAPTCHA API");
                return false;
            }

            boolean success = Boolean.TRUE.equals(body.get("success"));
            if (!success) {
                log.warn("reCAPTCHA falló. error-codes: {}", body.get("error-codes"));
            }
            return success;
        } catch (Exception ex) {
            log.error("Error al verificar reCAPTCHA: {}", ex.getMessage());
            return false;
        }
    }
}
