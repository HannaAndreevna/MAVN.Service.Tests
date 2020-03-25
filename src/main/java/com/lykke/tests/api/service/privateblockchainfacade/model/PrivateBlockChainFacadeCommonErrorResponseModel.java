package com.lykke.tests.api.service.privateblockchainfacade.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateBlockChainFacadeCommonErrorResponseModel {

    @JsonProperty("amount")
    private String[] amount;
    @JsonProperty("Amount")
    private String[] amount2;
    @JsonProperty("ModelErrors.Amount[0]")
    private String[] modelErrorsAmount;
    @JsonProperty("customerId")
    private String[] customerId;
    @JsonProperty("CustomerId")
    private String[] customerId2;
    @JsonProperty("ModelErrors.customerId[0]")
    private String[] modelErrorsCustomerId;
    @JsonProperty("RewardRequestId")
    private String[] rewardRequestId;
    @JsonProperty("senderCustomerId")
    private String[] senderCustomerId;
    @JsonProperty("recipientCustomerId")
    private String[] recipientCustomerId;
    @JsonProperty("Error")
    private String error;
    @JsonProperty("ModelErrors")
    private ModelErrors modelErrors;

    public String[] getAmount() {
        return null != amount
                ? amount
                : amount2;
    }

    public String[] getCustomerId() {
        return null != customerId
                ? customerId
                : customerId2;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ModelErrors {

        @JsonProperty("Amount")
        private String[] amount;
        @JsonProperty("customerId")
        private String[] customerId;
    }
}
