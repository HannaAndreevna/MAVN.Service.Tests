package com.lykke.tests.api.common.enums.campaign;

public enum SortBy {
    CAMPAIGN_NAME("CampaignName"),
    CREATION_DATE("CreationDate");

    private String sortBy;

    SortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getValue() {
        return this.sortBy;
    }
}
