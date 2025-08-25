package com.dress.dressrenting.exception.exceptions;

public class UnMatchedPasswordException extends RuntimeException{
    public UnMatchedPasswordException(String message) {
        super(message);
    }
}
