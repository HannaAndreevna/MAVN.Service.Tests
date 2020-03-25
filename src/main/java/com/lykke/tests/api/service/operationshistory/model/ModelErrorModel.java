package com.lykke.tests.api.service.operationshistory.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModelErrorModel {

    @JsonProperty("toDate")
    private String[] toDate;

    @JsonProperty("fromDate")
    private String[] fromDate;
}
