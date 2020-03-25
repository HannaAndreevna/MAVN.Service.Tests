package com.lykke.tests.api.service.admin.model.partners;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class LocationModel {

    private String name;
    private String address;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String externalId;
    private String accountingIntegrationCode;
}
