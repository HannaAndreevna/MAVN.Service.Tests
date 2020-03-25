package com.lykke.tests.api.service.campaigns.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ErrorCode {
    NONE("None", null),
    GUID_CANNOT_BE_PARSED("GuidCanNotBeParsed", "Invalid identifier passed");

    @Getter
    private String codeName;

    @Getter
    private String errorMessage;
}
