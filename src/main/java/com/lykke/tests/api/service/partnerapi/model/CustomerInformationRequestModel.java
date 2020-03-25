package com.lykke.tests.api.service.partnerapi.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class CustomerInformationRequestModel {

    private String customerId;
    private String email;
    private String phone;
}
