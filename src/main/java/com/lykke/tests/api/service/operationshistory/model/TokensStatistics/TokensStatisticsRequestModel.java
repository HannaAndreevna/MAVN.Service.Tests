package com.lykke.tests.api.service.operationshistory.model.TokensStatistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TokensStatisticsRequestModel {
    @JsonProperty("toDate")
    private String[] toDate;

    @JsonProperty("fromDate")
    private String[] fromDate;
}
