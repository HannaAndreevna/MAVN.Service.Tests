package com.lykke.tests.api.service.customerprofile.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class CustomerProfileUpdateRequestModel {

    private String customerId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int countryPhoneCodeId;
    private int countryOfResidenceId;
}
