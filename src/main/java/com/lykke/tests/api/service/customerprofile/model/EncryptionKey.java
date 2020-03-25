package com.lykke.tests.api.service.customerprofile.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class EncryptionKey {

    private String key;
}
