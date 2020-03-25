package com.lykke.tests.api.service.referral.model.referralleadmodel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class StatisticResponseModel {

    private String numberOfLeads;
    private String numberOfApprovedLeads;
    private String numberOfUniqueCompletedLeads;
}
