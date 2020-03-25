package com.lykke.tests.api.service.credentials.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
public class PartnerCredentialsCreateRequest extends PartnerCredentials {

    private String partnerId;

    @Builder(builderMethodName = "credentialsCreateRequestBuilder")
    public PartnerCredentialsCreateRequest(String clientId, String clientSecret, String partnerId) {
        super(clientId, clientSecret);
        this.partnerId = partnerId;
    }
}
