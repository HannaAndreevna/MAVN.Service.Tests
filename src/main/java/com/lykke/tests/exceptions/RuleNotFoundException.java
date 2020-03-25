package com.lykke.tests.exceptions;

public class RuleNotFoundException extends Exception {

    public RuleNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public RuleNotFoundException(String errorMessage, String id) {
        super(String.format(errorMessage, id));
    }
}
