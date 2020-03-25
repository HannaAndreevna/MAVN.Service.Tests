package com.lykke.tests.api.common.enums.campaign;

public enum  SortDirection {
    ASCENDING("Ascending"),
    DESCENDING("Descending");

    private String sortDirection;

    public String getValue() {
        return this.sortDirection;
    }

    SortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}
