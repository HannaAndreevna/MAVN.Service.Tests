package com.lykke.tests.api.service.campaigns.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorDescription {
    VALUE_IS_INVALID("The value '%s' is not valid.");

    private String value;

    public String getValue(String id) {
        return String.format(value, id);
    }
}
