package com.lykke.tests.api.service.admin.model.partners;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
public class LocationCreateRequest extends LocationModel {

    @Builder(builderMethodName = "locationCreateRequestBuilder")
    public LocationCreateRequest(String name, String address, String firstName, String lastName, String phone,
            String email, String externalId, String accountingIntegrationCode) {
        super(name, address, firstName, lastName, phone, email, externalId, accountingIntegrationCode);
    }
}
