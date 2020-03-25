package com.lykke.tests.api.service.partnermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerUpdateResponse extends PartnerManagementErrorResponseModel {

    @Builder(builderMethodName = "partnerUpdateResponseBuilder")
    public PartnerUpdateResponse(PartnerManagementError errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
