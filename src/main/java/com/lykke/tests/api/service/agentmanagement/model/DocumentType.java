package com.lykke.tests.api.service.agentmanagement.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
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
@PublicApi
public enum DocumentType {
    NONE("None"),
    PASSPORT("Passport"),
    VISA("Visa"),
    EMIRATES_ID("EmiratesId"),
    COMPANY_DOCS("CompanyDocs"),
    BANK_STATEMENT("BankStatement"),
    MOA("Moa"),
    BULK("Bulk"),
    OTHERS("Others");

    private static Map<String, DocumentType> FORMAT_MAP =
            Stream.of(DocumentType.values())
                    .collect(toMap(c -> c.getStatus(), Function.identity()));
    @Getter
    private String status;

    @JsonCreator
    public static DocumentType fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
