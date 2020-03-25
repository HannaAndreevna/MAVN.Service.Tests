package com.lykke.tests.exceptions;

public class UnableToRegisterUserException extends Exception {

    public UnableToRegisterUserException(String errorMessage) {
        super(errorMessage);
    }
}
