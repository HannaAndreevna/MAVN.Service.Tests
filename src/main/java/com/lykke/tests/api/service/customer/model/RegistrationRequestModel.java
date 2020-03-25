package com.lykke.tests.api.service.customer.model;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.api.testing.api.common.FakerUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.RegistrationData.COUNTRY_OF_NATIONALITY_ID_01;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class RegistrationRequestModel {

    private String email;
    private String referralCode;
    private String password;
    private String firstName;
    private String lastName;
    private int countryOfNationalityId;

    public RegistrationRequestModel() {
        email = generateRandomEmail();
        password = generateValidPassword();
        firstName = FakerUtils.firstName;
        lastName = FakerUtils.lastName;
        countryOfNationalityId = COUNTRY_OF_NATIONALITY_ID_01;
    }
}
