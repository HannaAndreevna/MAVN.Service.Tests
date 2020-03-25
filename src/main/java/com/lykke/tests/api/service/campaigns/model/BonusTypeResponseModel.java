package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.service.campaigns.CampaignServiceErrorResponseModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
@NetClassName("BonusTypeModel")
public class BonusTypeResponseModel extends CampaignServiceErrorResponseModel {

    private String type;
    private String displayName;
    private String vertical;
    private Boolean allowInfinite;
    private Boolean allowPercentage;
    private Boolean allowConversionRate;
    private Boolean isHidden;
}
