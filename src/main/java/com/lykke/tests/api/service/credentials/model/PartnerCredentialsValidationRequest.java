package com.lykke.tests.api.service.credentials.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
public class PartnerCredentialsValidationRequest extends PartnerCredentials {

    @Builder(builderMethodName = "credentialsUpdateRequestBuilder")
    public PartnerCredentialsValidationRequest(String clientId, String clientSecret) {
        super(clientId, clientSecret);
    }
}
