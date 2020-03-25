package com.lykke.tests.api.service.agentmanagement.model;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class ValidationErrorResponseModelEmulator {

    public static final String IMAGES_FIELD = "Images";
    public static final String BANK_INFO_FIELD = "BankInfo";
    public static final String BANK_INFO_BANK_NAME_FIELD = "BankInfo.BankName";
    public static final String BANK_INFO_BANK_BRANCH_FIELD = "BankInfo.BankBranch";
    public static final String BANK_INFO_BANK_ADDRESS_FIELD = "BankInfo.BankAddress";
    public static final String BANK_INFO_ACCOUNT_NUMBER_FIELD = "BankInfo.AccountNumber";
    public static final String BANK_INFO_BENEFICIARY_NAME_FIELD = "BankInfo.BeneficiaryName";
    public static final String BANK_INFO_BANK_BRANCH_COUNTRY_ID_FIELD = "BankInfo.BankBranchCountryId";
    public static final String IMAGES_0_DOCUMENT_TYPE_FIELD = "images[0].DocumentType";

    private static final String BANK_INFORMATION_REQUIRED_MESSAGE = "Bank information required.";
    private static final String IMAGES_REQUIRED_MESSAGE = "Images required";
    private static final String BANK_NAME_REQUIRED_MESSAGE = "Bank name required.";
    private static final String BRANCH_NAME_REQUIRED_MESSAGE = "Branch name required.";
    private static final String BANK_ADDRESS_REQUIRED_MESSAGE = "Bank address required.";
    private static final String ACCOUNT_NUMBER_REQUIRED_MESSAGE = "Account number required.";
    private static final String BENEFICIARY_NAME_REQUIRED_MESSAGE = "Beneficiary name required.";
    private static final String BANK_BRANCH_COUNTRY_REQUIRED_MESSAGE = "Bank branch country required.";
    private static final String THE_INPUT_WAS_NOT_VALID_MESSAGE = "The input was not valid.";

    private static final Map<String, String> ERROR_MESSAGES_COLLECTION =
            Stream.of(
                    new String[]{IMAGES_FIELD, IMAGES_REQUIRED_MESSAGE},
                    new String[]{BANK_INFO_FIELD, BANK_INFORMATION_REQUIRED_MESSAGE},
                    new String[]{BANK_INFO_BANK_NAME_FIELD, BANK_NAME_REQUIRED_MESSAGE},
                    new String[]{BANK_INFO_BANK_BRANCH_FIELD, BRANCH_NAME_REQUIRED_MESSAGE},
                    new String[]{BANK_INFO_BANK_ADDRESS_FIELD, BANK_ADDRESS_REQUIRED_MESSAGE},
                    new String[]{BANK_INFO_ACCOUNT_NUMBER_FIELD, ACCOUNT_NUMBER_REQUIRED_MESSAGE},
                    new String[]{BANK_INFO_BENEFICIARY_NAME_FIELD, BENEFICIARY_NAME_REQUIRED_MESSAGE},
                    new String[]{BANK_INFO_BANK_BRANCH_COUNTRY_ID_FIELD, BANK_BRANCH_COUNTRY_REQUIRED_MESSAGE},
                    new String[]{IMAGES_0_DOCUMENT_TYPE_FIELD, THE_INPUT_WAS_NOT_VALID_MESSAGE}
            )
                    .collect(toMap(item -> item[0], item -> item[1]));

    public Map<String, String> getErrorMessages(String... fieldNames) {
        if (null == fieldNames) {
            return new HashMap<>();
        }

        val fieldNamesList = Arrays.stream(fieldNames)
                .collect(toList());

        return ERROR_MESSAGES_COLLECTION
                .entrySet()
                .stream()
                .filter(item -> fieldNamesList.contains(item.getKey()))
                .collect(toMap(item -> item.getKey(), item -> item.getValue()));
    }
}
