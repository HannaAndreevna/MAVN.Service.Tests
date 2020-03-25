package com.lykke.tests.api.service.operationshistory.model.TokensStatistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModelErrorModel {
    @JsonProperty("dateTo")
    private String[] dateTo;

    @JsonProperty("dateFrom")
    private String[] dateFrom;
}
