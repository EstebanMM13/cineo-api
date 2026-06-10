package com.estebanmm13.auth_service.error;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resource, String field, Object value) {
        super(String.format("%s already exist with %s: '%s'", resource, field, value));
    }
}
