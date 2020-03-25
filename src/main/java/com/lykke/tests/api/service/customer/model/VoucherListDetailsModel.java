package com.lykke.tests.api.service.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class VoucherListDetailsModel extends VoucherModel {

    @Builder(builderMethodName = "voucherListDetailsModelBuilder")
    public VoucherListDetailsModel(String code, String spendRuleName, String partnerName, String priceToken,
            Double priceBaseCurrency, Date purchaseDate) {
        super(code, spendRuleName, partnerName, priceToken, priceBaseCurrency, purchaseDate);
    }
}
