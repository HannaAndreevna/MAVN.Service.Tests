package com.lykke.tests.api.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DocumentType {
    NONE("None"),
    PASSPORT("Passport"),
    VISA("Visa"),
    EMIRATES_ID("EmiratesId"),
    COMPANY_DOCS("CompanyDocs"),
    BANK_STATEMENTS("BankStatements"),
    MOA("Moa"),
    BULK("Bulk"),
    OTHERS("Others");

    @Getter
    private String value;
}
