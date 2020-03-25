package com.lykke.tests.api.service.customermanagement;

import static com.lykke.tests.api.base.Paths.CustomerManagement.EMAIL_VERIFICATION_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.customermanagement.model.VerificationCodeRequestModel;
import com.lykke.tests.api.service.customermanagement.model.VerificationCodeResponseModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EmailVerificationUtils {

    public VerificationCodeResponseModel requestToVerifyEmail(String customerId) {
        return getHeader(getAdminToken())
                .body(VerificationCodeRequestModel
                        .builder()
                        .customerId(customerId)
                        .build())
                .post(EMAIL_VERIFICATION_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(VerificationCodeResponseModel.class);
    }
}
