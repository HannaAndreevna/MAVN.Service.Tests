package com.lykke.tests.api.service.campaigns.model.burnrules;

import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
@PublicApi
@NetClassName("BurnRuleContentCreateRequest")
public class BurnRuleContentCreateRequestModel {

    private RuleContentType ruleContentType;
    private Localization localization;
    private String value;

    public String getLocalization() {
        return localization.getCode();
    }

    public String getRuleContentType() {
        return ruleContentType.getCode();
    }
}
