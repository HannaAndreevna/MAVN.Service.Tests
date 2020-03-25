package com.lykke.tests.api.common.model;

import static com.lykke.tests.api.common.CommonConsts.EMPTY_PASSWORD_ERR_MSG;
import static com.lykke.tests.api.common.CommonConsts.INVALID_PASSWORD_ERR;
import static com.lykke.tests.api.common.CommonConsts.INVALID_PASSWORD_ERR_MSG;
import static com.lykke.tests.api.common.CommonConsts.PASSWORD_REG_EX;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Builder
@Data
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
}
