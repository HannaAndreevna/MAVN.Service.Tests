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
public class PartnerListResponseModel extends PartnerManagementErrorResponseModel {

    private PartnerListDetailsModel[] partnersDetails;
    private int currentPage;
    private int totalSize;

    @Builder(builderMethodName = "partnerListResponseBuilder")
    public PartnerListResponseModel(PartnerManagementError errorCode, String errorMessage,
            PartnerListDetailsModel[] partnersDetails, int currentPage, int totalSize) {
        super(errorCode, errorMessage);
        this.partnersDetails = partnersDetails;
        this.currentPage = currentPage;
        this.totalSize = totalSize;
    }
}
