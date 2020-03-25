package com.lykke.tests.api.service.customerprofile.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@PublicApi
@NetClassName("CustomerProfileErrorCodes")
public enum CustomerProfileErrorCode {
    NONE("None"),
    CUSTOMER_PROFILE_DOES_NOT_EXIST("CustomerProfileDoesNotExist"),
    CUSTOMER_PRODILE_ALREADY_EXISTS("CustomerProfileAlreadyExists"),
    CUSTOMER_PROFILE_EMAIL_ALREADY_VERIFIED("CustomerProfileEmailAlreadyVerified"),
    CUSTOMER_PROFILE_ALREADY_EXISTS_WITH_DIFFERENT_PROVIDER("CustomerProfileAlreadyExistsWithDifferentProvider"),
    INVALID_COUNTRY_OF_NATIONALITY_ID("InvalidCountryOfNationalityId");

    private static Map<String, CustomerProfileErrorCode> FORMAT_MAP =
            Stream.of(CustomerProfileErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static CustomerProfileErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
