package com.lykke.tests.api.service.customer.model.wallets;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class ApproveExternalWalletLinkRequest {

    private String privateAddress;
    private String publicAddress;
    private String signature;
}
