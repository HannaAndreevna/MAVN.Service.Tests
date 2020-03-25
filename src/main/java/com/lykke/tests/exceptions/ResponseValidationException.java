package com.lykke.tests.exceptions;

public class ResponseValidationException extends Exception {

    public ResponseValidationException(String errorMessage) {
        super(errorMessage);
    }
}
