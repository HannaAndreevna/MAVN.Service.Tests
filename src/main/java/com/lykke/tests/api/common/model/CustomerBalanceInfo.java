package com.lykke.tests.api.common.model;

import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;

import com.lykke.tests.api.service.customermanagement.model.register.LoginProvider;
import lombok.Builder;
import lombok.Data;

@Data
public class CustomerBalanceInfo extends CustomerInfo {

    private Double newAmount;
    private Double extraAmount;
    private String campaignId;
    private String conditionId;

    @Builder(builderMethodName = "customerBalanceInfoBuilder")
    public CustomerBalanceInfo(String firstName, String lastName, String phoneNumber, int countryPhoneCodeId,
            String email, String referralCode, String password, LoginProvider loginProvider, int countryOfNationalityId,
            String customerId, String token, Double newAmount, Double extraAmount, String campaignId,
            String conditionId) {
        super(firstName, lastName, phoneNumber, countryPhoneCodeId, email, referralCode, password, loginProvider,
                countryOfNationalityId, customerId, token);
        this.newAmount = newAmount;
        this.extraAmount = extraAmount;
        this.campaignId = campaignId;
        this.conditionId = conditionId;
    }

    public String getToken() {
        return getUserToken(super.getEmail(), super.getPassword());
    }
}
