package com.lykke.tests.api.service.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class VoucherModel {

    private String code;
    private String spendRuleName;
    private String partnerName;
    private String priceToken;
    private Double priceBaseCurrency;
    private Date purchaseDate;
}
