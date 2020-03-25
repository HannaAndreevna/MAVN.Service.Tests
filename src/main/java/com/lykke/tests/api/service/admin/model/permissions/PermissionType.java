package com.lykke.tests.api.service.admin.model.permissions;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public enum PermissionType {
    DASHBOARD("Dashboard"),
    CUSTOMERS("Customers"),
    ACTION_RULES("ActionRules"),
    REPORTS("Reports"),
    BLOCKCHAIN_OPERATIONS("BlockchainOperations"),
    PROGRAM_PARTNERS("ProgramPartners"),
    SETTINGS("Settings"),
    ADMIN_USERS("AdminUsers");

    private static Map<String, PermissionType> FORMAT_MAP =
            Stream.of(PermissionType.values())
                    .collect(toMap(r -> r.getType(), Function.identity()));
    @Getter
    private String type;

    @JsonCreator
    public static PermissionType fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
