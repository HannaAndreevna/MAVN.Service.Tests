package com.lykke.tests.api.service.customerprofile.model.admins;

import com.lykke.api.testing.annotations.NetClassName;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.customerprofile.model.CustomerProfileErrorCode;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@PublicApi
@NetClassName("AdminProfileErrorCodes")
public enum AdminProfileErrorCode {
    NONE("None"),
    ADMIN_PROFILE_DOES_NOT_EXIST("AdminProfileDoesNotExist"),
    ADMIN_PROFILE_ALREADY_EXISTS("AdminProfileAlreadyExists");

    private static Map<String, AdminProfileErrorCode> FORMAT_MAP =
            Stream.of(AdminProfileErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @JsonCreator
    public static AdminProfileErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
