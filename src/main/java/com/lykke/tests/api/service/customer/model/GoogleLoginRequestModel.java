package com.lykke.tests.api.service.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class GoogleLoginRequestModel {

    private String accessToken;
}
