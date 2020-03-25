package com.lykke.tests.api.service.customer.model.agents;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@Builder
@PublicApi
public class BankInfoModel {
    private String beneficiaryName;
    private String bankName;
    private String bankBranch;
    private String accountNumber;
    private String bankAddress;
    private int bankBranchCountryId;
    private String iban;
    private String swift;
}
