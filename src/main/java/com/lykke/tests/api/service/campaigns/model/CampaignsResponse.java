package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class CampaignsResponse {
    // TODO: probably, it's worng type here
    private CampaignResponse[] campaigns;
    // TODO
    // private ErrorCode errorCode;
    private String errorCode;
    private String errorMessage;

    public CampaignsResponse() {
        campaigns = new CampaignResponse[]{};
    }
}
