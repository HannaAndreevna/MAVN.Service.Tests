package com.lykke.tests.api.service.partnermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerCreateResponse extends PartnerManagementErrorResponseModel {

    @Exclude
    private String id;

    @Builder(builderMethodName = "partnerCreateResponseBuilder")
    public PartnerCreateResponse(PartnerManagementError errorCode, String errorMessage, String id) {
        super(errorCode, errorMessage);
        this.id = id;
    }
}
