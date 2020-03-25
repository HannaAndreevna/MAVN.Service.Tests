package com.lykke.tests.api.service.partnermanagement.model;

import lombok.Builder;
import lombok.Data;

@Data
public class LocationCreateModel extends LocationBaseModel {

    @Builder(builderMethodName = "locationBuilder")
    public LocationCreateModel(String name, String address, String externalId, ContactPersonModel contactPerson,
            String accountingIntegrationCode) {
        super(name, address, externalId, contactPerson, accountingIntegrationCode);
    }
}
