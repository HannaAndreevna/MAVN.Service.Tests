package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
@NetClassName("EarnRuleContentCreateRequest")
public class EarnRule {

    private RuleContentType ruleContentType;
    private Localization localization;
    private String value;

    public String getRuleContentType() {
        return ruleContentType.getCode();
    }

    public RuleContentType getRuleContentTypeValue() {
        return ruleContentType;
    }
}
