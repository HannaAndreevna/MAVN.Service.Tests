package com.lykke.tests.api.service.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class EmailVerificationRequest {

    private String verificationCode;
}
