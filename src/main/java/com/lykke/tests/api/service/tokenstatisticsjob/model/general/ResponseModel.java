package com.lykke.tests.api.service.tokenstatisticsjob.model.general;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@NetClassName("TotalTokensResponse")
@PublicApi
public class ResponseModel {

    private String totalTokensAmount;
    private TokensErrorCode errorCode;
}
