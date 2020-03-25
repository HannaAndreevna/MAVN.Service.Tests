package com.lykke.tests.api.service.campaigns.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum CampaignStatus {
    PENDING("Pending"),
    ACTIVE("Active"),
    COMPLETED("Completed"),
    INACTIVE("Inactive");

    private static Map<String, CampaignStatus> FORMAT_MAP =
            Stream.of(CampaignStatus.values())
                    .collect(toMap(c -> c.getStatus(), Function.identity()));
    @Getter
    private String status;

    @JsonCreator
    public static CampaignStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
