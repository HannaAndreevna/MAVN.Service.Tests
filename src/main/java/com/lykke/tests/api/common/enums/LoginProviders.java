package com.lykke.tests.api.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LoginProviders {
    STANDARD("Standard"),
    GOOGLE("Google");

    @Getter
    private String value;
}
