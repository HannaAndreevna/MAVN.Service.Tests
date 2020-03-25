package com.lykke.tests.api.service.customermanagement.model.register;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.RegistrationData.COUNTRY_OF_NATIONALITY_ID_01;

import com.lykke.api.testing.api.common.FakerUtils;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegistrationRequestModel {

    public String firstName;
    public String lastName;
    public String phoneNumber;
    public int countryPhoneCodeId;
    private String email;
    private String referralCode;
    private String password;
    private LoginProvider loginProvider;
    private int countryOfNationalityId;

    public RegistrationRequestModel() {
        firstName = FakerUtils.firstName;
        lastName = FakerUtils.lastName;
        phoneNumber = FakerUtils.phoneNumber;
        countryPhoneCodeId = FakerUtils.countryPhoneCode;
        email = generateRandomEmail();
        password = generateValidPassword();
        loginProvider = LoginProvider.STANDARD;
        countryOfNationalityId = COUNTRY_OF_NATIONALITY_ID_01;
    }

    @Builder(builderClassName = "completeCustomer", builderMethodName = "completeCustomerBuilder")
    public RegistrationRequestModel(String firstName, String lastName, String phoneNumber, int countryPhoneCodeId,
            String email, String referralCode, String password, LoginProvider loginProvider,
            int countryOfNationalityId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.countryPhoneCodeId = countryPhoneCodeId;
        this.email = email;
        this.referralCode = referralCode;
        this.password = password;
        this.loginProvider = loginProvider;
        this.countryOfNationalityId = COUNTRY_OF_NATIONALITY_ID_01;
    }

    @Builder(builderClassName = "minimumCustomer", builderMethodName = "minimumCustomerBuilder")
    public RegistrationRequestModel(String email, String password) {
        this.email = email;
        this.password = password;
        this.loginProvider = LoginProvider.STANDARD;
        this.countryOfNationalityId = COUNTRY_OF_NATIONALITY_ID_01;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
