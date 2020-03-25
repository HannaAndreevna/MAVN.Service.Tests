package com.lykke.tests.api.service.credentials.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public abstract class PartnerCredentials {

    private String clientId;
    private String clientSecret;
}
