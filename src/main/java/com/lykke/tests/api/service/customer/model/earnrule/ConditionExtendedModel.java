package com.lykke.tests.api.service.customer.model.earnrule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.customer.model.spendrule.PartnerModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class ConditionExtendedModel {

    private String id;
    private String type;
    private String displayName;
    private String immediateReward;
    private int completionCount;
    private int customerCompletionCount;
    private PartnerModel[] partners;
}
