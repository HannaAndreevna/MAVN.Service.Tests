package com.lykke.tests.api.service.admin.model.partners;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.common.enums.BusinessVertical;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@PublicApi
public class PartnerBaseModel {

    private String name;
    private String amountInTokens;
    private Double amountInCurrency;
    private boolean useGlobalCurrencyRate;
    private String description;
    private String clientId;
    private String clientSecret;
    private BusinessVertical businessVertical;
}
