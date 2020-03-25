package com.lykke.tests.api.service.customermanagement.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class VerificationCodeRequestModel {

    private String customerId;
}
