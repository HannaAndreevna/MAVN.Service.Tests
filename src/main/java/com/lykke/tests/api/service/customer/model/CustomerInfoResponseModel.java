package com.lykke.tests.api.service.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class CustomerInfoResponseModel {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private boolean isEmailVerified;
    private boolean isAccountBlocked;
    private boolean isPhoneNumberVerified;
    private String countryPhoneCode;
    private int countryPhoneCodeId;
    private int countryOfNationalityId;
    private String countryOfNationalityName;
}
