package com.lykke.tests.api.service.bonuscustomerprofile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.val;

@Data
public class BonusCustomerProfileResponse {
    @JsonProperty("ErrorCode")
    private String errorCodeString;
    @JsonProperty("ErrorMessage")
    private String errorMessage;
    @JsonProperty("ContributionIds")
    private List<String> contributionIds;

    public BonusCustomerProfileResponse() {
        contributionIds = new ArrayList<>();
    }

    public Optional<ErrorCode> getErrorCode() {
        val errorCode = Optional.of(Arrays.stream(ErrorCode.values())
                .filter(item -> errorCodeString.equals(item.getCodeName()))
                .findFirst());

        return errorCode.orElseGet(Optional::empty);
    }
}

