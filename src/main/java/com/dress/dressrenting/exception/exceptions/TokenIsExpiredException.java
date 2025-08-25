package com.dress.dressrenting.exception.exceptions;

public class TokenIsExpiredException extends RuntimeException {
    public TokenIsExpiredException(String message) {
        super(message);
    }
}
