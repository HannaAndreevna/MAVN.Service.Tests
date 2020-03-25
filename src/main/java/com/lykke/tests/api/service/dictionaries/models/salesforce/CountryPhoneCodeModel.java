package com.lykke.tests.api.service.dictionaries.models.salesforce;

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
@PublicApi
public class CountryPhoneCodeModel {

    private String id;
    private String code;
    private String isoCode;
    private String countryName;
    private String countryIso2Code;
    private String countryIso3Code;
}
