package com.lykke.tests.api.service.crosschainwalletlinker.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class LinkApprovalRequestModel {

    private String privateAddress;
    private String publicAddress;
    private String signature;
}
