package com.lykke.tests.api.service.admin.model.blockchain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseTransaction {

    private String blockHash;
    private long blockNumber;
    private String transactionHash;
    private long transactionIndex;
    private String from;
    private String to;
    private int status;
}
