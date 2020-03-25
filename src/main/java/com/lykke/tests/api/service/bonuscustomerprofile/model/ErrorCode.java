package com.lykke.tests.api.service.bonuscustomerprofile.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ErrorCode {
    NONE("None", null, new ArrayList<>()),
    // TODO: fill in later - currently not in use in the product
    ENTITY_NOT_FOUND("EntityNotFound", "", new ArrayList<>()),
    GUID_CANNOT_BE_PARSED("GuidCanNotBeParsed", "Invalid identifier", null),
    ENTITY_NOT_VALID("EntityNotValid", "", new ArrayList<>());

    @Getter
    private String codeName;

    @Getter
    private String errorMessage;

    @Getter
    private List<UUID> ids;
}
