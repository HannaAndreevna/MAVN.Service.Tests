package com.lykke.tests.api.common.enums;

import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CountryPhoneCodes {

    BELARUS(375),
    Russia(7),
    Bolivia(591),
    Fiji(679),
    Switzerland(41),
    Saudi_Arabia(966),
    Georgia(995),
    Malta(679),
    Turkey(90);

    @Getter
    public int countryCode;

    private static final int SIZE = values().length;
    private static final Random RANDOM = new Random();

    public static int randomCountryCode() {
        return values()[RANDOM.nextInt(SIZE)].getCountryCode();
    }
}
