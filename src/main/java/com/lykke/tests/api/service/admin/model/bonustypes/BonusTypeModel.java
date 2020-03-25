package com.lykke.tests.api.service.admin.model.bonustypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class BonusTypeModel {

    private String type;
    private String displayName;
    private Vertical vertical;
    private boolean allowInfinite;
    private boolean allowPercentage;
    private boolean allowConversionRate;
    private boolean isStakeable;
}
