package com.lykke.tests.api.service.operationshistory.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BonusCashInResponse {

    private String transactionId;
    private String externalOperationId;
    private String customerId;
    private String assetSymbol;
    private String amount;
    private String bonusType;
    private Date timestamp;
    private String partnerId;
    private String campaignName;
    private String conditionName;
}
