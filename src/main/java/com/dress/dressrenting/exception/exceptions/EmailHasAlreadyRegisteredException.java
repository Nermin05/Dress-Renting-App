package com.dress.dressrenting.exception.exceptions;

public class EmailHasAlreadyRegisteredException extends RuntimeException{
    public EmailHasAlreadyRegisteredException(String message) {
        super(message);
    }
}
