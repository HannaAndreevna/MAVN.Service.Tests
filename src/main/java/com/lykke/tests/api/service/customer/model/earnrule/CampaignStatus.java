package com.lykke.tests.api.service.customer.model.earnrule;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@PublicApi
public enum CampaignStatus {
    PENDING("Pending", "Represents status of the Campaign that has not begin yet"),
    ACTIVE("Active", "Represents status of the Campaign that is currently Active"),
    COMPLETED("Completed", "Represents status of the Campaign that is already Completed"),
    INACTIVE("Inactive", "Represents status of the Campaign that has been manually deleted");

    private static Map<String, CampaignStatus> FORMAT_MAP =
            Stream.of(CampaignStatus.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String description;

    @JsonCreator
    public static CampaignStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}