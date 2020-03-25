package com.lykke.tests.api.service.admin.model.blockchain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventModel {

    private String blockHash;
    private long blockNumber;
    private String transactionHash;
    private long transactionIndex;
    private long logIndex;
    private String address;
    private String eventName;
    private String eventSignature;
    private EventParameters[] parameters;
    private Date timestamp;
}
