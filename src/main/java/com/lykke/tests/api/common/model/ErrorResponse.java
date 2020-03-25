package com.lykke.tests.api.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(LowerCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {

    private String errorMessage;
    private Map<String, List<String>> modelErrors;
}

