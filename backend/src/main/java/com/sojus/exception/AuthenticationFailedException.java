package com.sojus.exception;

/**
 * Excepción para errores de autenticación.
 * Genera respuesta HTTP 401 Unauthorized.
 */
public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(String message) {
        super(message);
    }
}
