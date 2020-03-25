package com.lykke.tests.api.service.admin.model.partners;

import lombok.Builder;
import lombok.Data;

@Data
public class LocationEditRequest extends LocationModel {

    private String id;

    @Builder(builderMethodName = "locationEditRequestBuilder")
    public LocationEditRequest(String name, String address, String firstName, String lastName, String phone,
            String email, String externalId, String accountingIntegrationCode, String id) {
        super(name, address, firstName, lastName, phone, email, externalId, accountingIntegrationCode);
        this.id = id;
    }
}
