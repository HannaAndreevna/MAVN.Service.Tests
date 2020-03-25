package com.lykke.tests.api.service.admin.model.dashboard.tokenstatistics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokensListResponse {

    private TokensStatistics[] earn;
    private TokensStatistics[] burn;
    private TokensStatistics[] walletBalance;
    private String totalEarn;
    private String totalBurn;
    private String totalWalletBalance;
}
