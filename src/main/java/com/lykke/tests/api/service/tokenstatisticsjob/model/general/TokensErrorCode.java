package com.lykke.tests.api.service.tokenstatisticsjob.model.general;


import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lykke.api.testing.annotations.NetClassName;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@NetClassName("TokensErrorCodes")
public enum TokensErrorCode {
    NONE("None", "no error"),
    STATISTICS_NOT_FOUND("StatisticsNotFound", "Tokens statistics were not found"),
    PRIVATE_BLOCKCHAIN_FACADE_IS_NOT_AVAILABLE("PrivateBlockchainFacadeIsNotAvailable",
            "The system was not able to sync total amount value with the one from PBF Service");

    private static Map<String, TokensErrorCode> FORMAT_MAP =
            Stream.of(TokensErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static TokensErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
