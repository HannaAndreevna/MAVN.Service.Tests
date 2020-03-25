package com.lykke.tests.api.service.admin.model.partners;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerRowResponse {

    private String id;
    private String name;
    private int tokensRate;
    private int currencyRate;
    private String createdAt;
    private String createdBy;
    private String businessVertical;
}
