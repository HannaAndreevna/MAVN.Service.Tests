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
public class Event {

    private String blockHash;
    private long blockNumber;
    private String transactionHash;
    private long transactionIndex;
    private long logIndex;
    private String address;
    private String eventName;
    private String eventSignature;
    private long timestamp;
    private EventParameter[] parameters;
}
