package com.lykke.tests.api.service.customerprofile.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class CustomerProfile {

    private String customerId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String shortPhoneNumber;
    private Date registered;
    @JsonProperty("IsEmailVerified")
    private boolean isEmailVerified;
    private String countryPhoneCodeId;
    private String countryOfResidenceId;
    private int countryOfNationalityId;
    private String tierId;
    private LoginProvider[] loginProviders;
}
