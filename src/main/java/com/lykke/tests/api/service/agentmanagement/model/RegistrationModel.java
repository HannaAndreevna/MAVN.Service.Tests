package com.lykke.tests.api.service.agentmanagement.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class RegistrationModel {

    private String customerId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int countryPhoneCodeId;
    private int countryOfResidenceId;
    private String note;
    private BankInfoModel bankInfo;
    private ImageModel[] images;
}
