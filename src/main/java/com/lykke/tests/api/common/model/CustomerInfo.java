package com.lykke.tests.api.common.model;

import com.lykke.tests.api.service.customermanagement.model.register.LoginProvider;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerInfo extends RegistrationRequestModel {

    private String customerId;
    private String token;

    @Builder(builderMethodName = "customerInfoBuilder")
    public CustomerInfo(String firstName, String lastName, String phoneNumber, int countryPhoneCodeId, String email,
            String referralCode, String password, LoginProvider loginProvider, int countryOfNationalityId,
            String customerId, String token) {
        super(firstName, lastName, phoneNumber, countryPhoneCodeId, email, referralCode, password, loginProvider,
                countryOfNationalityId);
        this.customerId = customerId;
        this.token = token;
    }
}
