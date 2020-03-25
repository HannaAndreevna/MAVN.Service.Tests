package com.lykke.tests.api.common;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

import com.lykke.tests.api.common.model.ResetPasswordRequest;
import com.lykke.tests.api.common.model.ValidationErrorResponseModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResetPasswordUtils {

    public ValidationErrorResponseModel getValidationErrorResponse(ResetPasswordRequest requestObject,
            String endpointPath) {
        return getHeader()
                .body(requestObject)
                .post(endpointPath)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponseModel.class);
    }
}
