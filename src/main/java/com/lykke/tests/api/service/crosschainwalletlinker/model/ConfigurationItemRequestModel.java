package com.lykke.tests.api.service.crosschainwalletlinker.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class ConfigurationItemRequestModel {

    private ConfigurationItemType type;
    private String value;

    public String getType() {
        return type.getCode();
    }

    public ConfigurationItemType getEnumType() {
        return type;
    }
}
