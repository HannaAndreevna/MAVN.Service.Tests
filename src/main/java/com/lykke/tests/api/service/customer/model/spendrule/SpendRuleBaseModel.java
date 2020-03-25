package com.lykke.tests.api.service.customer.model.spendrule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.common.enums.BusinessVertical;
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
public class SpendRuleBaseModel {

    private String id;
    private String title;
    private String currencyName;
    private String description;
    private String imageUrl;
    private BusinessVertical businessVertical;
    private Date creationDate;
    private Double price;
    private long stockCount;
    private long soldCount;
    private int order;
}
