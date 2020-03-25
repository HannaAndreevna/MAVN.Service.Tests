package com.lykke.tests.api.service.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class SetCustomerPhoneInfoRequestModel {

    private String customerId;
    private String phoneNumber;
    private int countryPhoneCodeId;
}
