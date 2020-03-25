package com.lykke.tests.api.service.dashboardstatistics.models.tokens;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokensListResponseModel {

    private TokensStatisticsModel[] earn;
    private TokensStatisticsModel[] burn;
    private TokensStatisticsModel[] walletBalance;
    private String totalEarn;
    private String totalBurn;
    private String totalWalletBalance;
}
