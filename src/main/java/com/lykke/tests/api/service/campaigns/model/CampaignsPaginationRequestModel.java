package com.lykke.tests.api.service.campaigns.model;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.api.testing.annotations.QueryParameters;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
@QueryParameters
public class CampaignsPaginationRequestModel extends BasePaginationRequestModel {

    private String campaignName;
    private String conditionType;
    private CampaignStatus campaignStatus;
    private CampaignSortBy sortBy;
    private ListSortDirection sortDirection;

    @Builder(builderMethodName = "requestModelBuilder")
    public CampaignsPaginationRequestModel(int currentPage, int pageSize, String campaignName, String conditionType,
            CampaignStatus campaignStatus,
            CampaignSortBy sortBy,
            ListSortDirection sortDirection) {
        super(currentPage, pageSize);
        this.campaignName = campaignName;
        this.conditionType = conditionType;
        this.campaignStatus = campaignStatus;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    public String getCampaignStatus() {
        return campaignStatus.getStatus();
    }

    public String getSortBy() {
        return sortBy.getSortBy();
    }

    public String getSortDirection() {
        return sortDirection.getDirection();
    }
}
