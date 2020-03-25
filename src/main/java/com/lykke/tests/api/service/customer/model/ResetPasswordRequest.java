package com.lykke.tests.api.service.customer.model;

import static com.lykke.tests.api.common.CommonConsts.EMPTY_PASSWORD_ERR_MSG;
import static com.lykke.tests.api.common.CommonConsts.INVALID_PASSWORD_ERR;
import static com.lykke.tests.api.common.CommonConsts.INVALID_PASSWORD_ERR_MSG;
import static com.lykke.tests.api.common.CommonConsts.PASSWORD_REG_EX;
import static com.lykke.tests.api.service.customer.model.VerificationCodeError.CUSTOMER_DOES_NOT_EXIST;

import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.tests.api.common.model.ValidationErrorResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Builder
@Data
@NetClassName("ResetPasswordRequestModel")
public class ResetPasswordRequest {

    String password;
    String customerEmail;
    String resetIdentifier;

    private boolean isPasswordValid() {
        return password.matches(PASSWORD_REG_EX);
    }

    public ValidationErrorResponseModel getInvalidPasswordResponse() {
        val passwordErrMsg = StringUtils.EMPTY == password
                ? EMPTY_PASSWORD_ERR_MSG
                : INVALID_PASSWORD_ERR_MSG;
        val response = new ValidationErrorResponseModel();
        response.setError(isPasswordValid()
                ? null
                : INVALID_PASSWORD_ERR);
        response.setMessage(isPasswordValid()
                ? null
                : passwordErrMsg);
        return response;
    }

    public ValidationErrorResponseModel getNonExistingCustomerResponse() {
        return ValidationErrorResponseModel
                .builder()
                .error(CUSTOMER_DOES_NOT_EXIST.getCode())
                .message(CUSTOMER_DOES_NOT_EXIST.getMessage())
                .build();
    }
}
