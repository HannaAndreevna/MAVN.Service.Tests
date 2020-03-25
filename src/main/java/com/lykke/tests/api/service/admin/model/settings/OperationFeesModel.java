package com.lykke.tests.api.service.admin.model.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class OperationFeesModel {

    private String crossChainTransferFee;
    private String firstTimeLinkingFee;
    private String subsequentLinkingFee;

    public String getCrossChainTransferFee() {
        return String.valueOf(Double.valueOf(crossChainTransferFee).intValue());
    }
}
