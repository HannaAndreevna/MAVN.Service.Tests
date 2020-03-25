package com.lykke.tests.api.service.customer.model.agents;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@Builder
@PublicApi
public class AgentRegistrationRequestModel {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int countryPhoneCodeId;
    private int countryOfResidenceId;
    private String note;
    private BankInfoModel bankInfo;
    private ImageModel[] images;
    private String token;
}
