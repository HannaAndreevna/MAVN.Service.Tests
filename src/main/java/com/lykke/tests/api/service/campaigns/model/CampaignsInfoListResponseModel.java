package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class CampaignsInfoListResponseModel extends BonusEngineErrorResponseModel {
    private CampaignInformationResponseModel[] campaigns;
}
