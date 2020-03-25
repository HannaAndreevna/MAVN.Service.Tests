package com.lykke.tests.api.service.campaigns.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
public enum CampaignSortBy {
    CAMPAIGN_NAME("CampaignName"),
    CREATION_DATE("CreationDate");

    private static Map<String, CampaignSortBy> FORMAT_MAP =
            Stream.of(CampaignSortBy.values())
                    .collect(toMap(c -> c.getSortBy(), Function.identity()));
    @Getter
    private String sortBy;

    @JsonCreator
    public static CampaignSortBy fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
