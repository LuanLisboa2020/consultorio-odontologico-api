package com.consultorio.consultorio_odontologico_api.exception;

public class RefreshTokenReuseException extends RuntimeException {

    public RefreshTokenReuseException() {
        super("Sessão encerrada por uma tentativa de reutilização de token.");
    }
}
