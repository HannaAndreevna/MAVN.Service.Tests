package com.lykke.tests.api.common.enums.campaign;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RewardType {
    FIXED("Fixed"),
    PERCENTAGE("Percentage");

    @Getter
    private String value;
}
