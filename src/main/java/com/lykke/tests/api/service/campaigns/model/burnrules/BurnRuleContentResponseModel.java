package com.lykke.tests.api.service.campaigns.model.burnrules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
@NetClassName("BurnRuleContentResponse")
public class BurnRuleContentResponseModel {

    private String id;
    private RuleContentType ruleContentType;
    private Localization localization;
    private String value;
    private FileResponseModel image;

    public String getLocalization() {
        return localization.getCode();
    }

    public String getRuleContentType() {
        return ruleContentType.getCode();
    }
}
