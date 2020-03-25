package com.lykke.tests.api.service.customer.model.lists;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@PublicApi
@NetClassName("CountryPhoneCodeModel")
public class CountryPhoneCodesResponse {

    private int id;
    private String code;
    private String countryName;
    private String countryIso2Code;
    private String countryIso3Code;
}
