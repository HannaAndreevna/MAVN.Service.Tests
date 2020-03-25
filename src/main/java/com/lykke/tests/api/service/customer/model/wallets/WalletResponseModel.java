package com.lykke.tests.api.service.customer.model.wallets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class WalletResponseModel {

    private String balance;
    private String externalBalance;
    private String assetSymbol;
    private Boolean isWalletBlocked;
    private String totalEarned;
    private String totalSpent;
    private String privateWalletAddress;
    private String publicWalletAddress;
    private PublicAddressLinkingStatus publicAddressLinkingStatus;
}
