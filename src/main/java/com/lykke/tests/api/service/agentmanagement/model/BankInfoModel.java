package com.lykke.tests.api.service.agentmanagement.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
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
