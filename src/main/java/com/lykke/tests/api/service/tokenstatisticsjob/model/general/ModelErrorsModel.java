package com.lykke.tests.api.service.tokenstatisticsjob.model.general;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelErrorsModel {

    @JsonProperty("dt")
    private String[] dt;
}
