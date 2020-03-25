package com.lykke.tests.api.service.credentials.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class GenerateClientIdRequest {

    private int length;
}
