package com.lykke.tests.api.service.customerprofile.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CustomerProfileRequestModel {

    private String customerId;
    private String email;
    private String firstName;
    private String lastName;
    private int countryOfNationalityId;
    private LoginProvider loginProvider;

    public String getLoginProvider() {
        return loginProvider.getCode();
    }
}
