package com.lykke.tests.api.service.operationshistory.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ActiveCustomerRequestModel {

    @JsonProperty("toDate")
    private String[] toDate;

    @JsonProperty("fromDate")
    private String[] fromDate;
}
