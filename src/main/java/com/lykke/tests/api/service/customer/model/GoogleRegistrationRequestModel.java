package com.lykke.tests.api.service.customer.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@PublicApi
public class GoogleRegistrationRequestModel {

    private String accessToken;
    private String referralCode;
    private String firstName;
    private String lastName;
    private int countryOfNationalityId;
}
