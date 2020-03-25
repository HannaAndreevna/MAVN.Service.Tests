package com.lykke.tests.api.service.customerprofile.statistics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelErrors {

    private String[] startDate;
    private String[] endDate;
}
