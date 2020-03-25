package com.lykke.tests.api.service.quorumexplorer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class Transaction {

    private String blockHash;
    private long blockNumber;
    private String transactionHash;
    private long transactionIndex;
    private String from;
    private String to;
    private String contractAddress;
    private int status;
    private String functionName;
    private String functionSignature;
    private long timestamp;
}