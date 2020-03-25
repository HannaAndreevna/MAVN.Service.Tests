package com.lykke.tests.api.service.customer.model.spendrule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.common.enums.BusinessVertical;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class SpendRuleListDetailsModel extends SpendRuleBaseModel {

    @Builder(builderMethodName = "spendRuleListDetailsModelBuilder")
    public SpendRuleListDetailsModel(String id, String title, String currencyName, String description, String imageUrl,
            BusinessVertical businessVertical, Date creationDate, Double price, long stockCount, long soldCount,
            int order) {
        super(id, title, currencyName, description, imageUrl, businessVertical, creationDate, price, stockCount,
                soldCount, order);
    }
}
