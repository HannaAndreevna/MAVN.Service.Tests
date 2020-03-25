package com.lykke.tests.api.common.enums.campaign;

public enum  CampaignStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    PENDING("Pending"),
    COMPLETED("Completed");

    private String campaignStatus;

    public String getValue() {
        return this.campaignStatus;
    }

    CampaignStatus(String sortBy) {
        this.campaignStatus = sortBy;
    }
}
