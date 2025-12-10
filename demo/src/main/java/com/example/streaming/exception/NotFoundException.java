package com.example.streaming.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String mensaje) {
        super(mensaje);
    }
}
