package com.lykke.tests.api.service.partnermanagement.model;

import lombok.Builder;
import lombok.Data;

@Data
public class LocationUpdateModel extends LocationBaseModel {

    private String id;

    @Builder(builderMethodName = "locationBuilder")
    public LocationUpdateModel(String name, String address, String externalId, ContactPersonModel contactPerson,
            String accountingIntegrationCode, String id) {
        super(name, address, externalId, contactPerson, accountingIntegrationCode);
        this.id = id;
    }
}
