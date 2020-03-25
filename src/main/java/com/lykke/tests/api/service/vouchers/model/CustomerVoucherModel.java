package com.lykke.tests.api.service.vouchers.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class CustomerVoucherModel {

    private String id;
    private String code;
    private VoucherStatus status;
    private String spendRuleId;
    private String amountInTokens;
    private Double amountInBaseCurrency;
    private Date purchaseDate;
}
