package com.lykke.tests.api.service.operationshistory.model.TokensStatistics;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(UpperCamelCaseStrategy.class)
public class TokensStatisticsResponse {
    private String asset;
    private int earnedAmount;
    private int burnedAmount;
}
