package com.lykke.tests.api.service.customer.model.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class EmailVerificationRequest {
    private String verificationCode;

}
