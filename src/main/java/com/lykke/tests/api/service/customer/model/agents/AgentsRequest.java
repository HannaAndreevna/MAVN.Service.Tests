package com.lykke.tests.api.service.customer.model.agents;

import static com.lykke.tests.api.common.CommonConsts.MODEL_VALIDATION_FAILURE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@Data
@PublicApi
@Builder
@AllArgsConstructor
public class AgentsRequest {

    private final String REQUIRED_FIRST_NAME_ERR_MSG = "The FirstName field is required.";
    private final String REQUIRED_LAST_NAME_ERR_MSG = "The LastName field is required.";
    private final String REQUIRED_PHONE_NUMBER_ERR_MSG = "The PhoneNumber field is required.";

    private String token;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int countryPhoneCodeId;
    private int countryOfResidenceId;
    private String note;
    private BankInfoModel bankInfo;
    private ImageModel[] images;

    private boolean isTokenValid() {
        return getToken().length() < 71;
    }

    private boolean isFirstNameNotEmpty() { return EMPTY != firstName; }

    private boolean isLastNameNotEmpty() { return lastName != EMPTY; }

    private boolean isPhoneNumberNotEmpty() {
        return phoneNumber != EMPTY;
    }

    // private boolean isCountryPhoneCodeId() {return countryPhoneCodeId }

    public int getHttpStatus() {
        if (!isTokenValid()) {
            return SC_UNAUTHORIZED;
        } else if (isFirstNameNotEmpty() || isLastNameNotEmpty() || isPhoneNumberNotEmpty()) {
            return SC_OK;
        } else {
            return SC_BAD_REQUEST;
        }
    }

    public AgentsValidationErrorResponseModel getValidationErrorResponse() {
        val response = new AgentsValidationErrorResponseModel();
        response.setError(
                isFirstNameNotEmpty() || isLastNameNotEmpty() || isPhoneNumberNotEmpty()
                        ? MODEL_VALIDATION_FAILURE
                        : null);
        response.setMessage(
                !isFirstNameNotEmpty()
                        ? REQUIRED_FIRST_NAME_ERR_MSG
                        : !isLastNameNotEmpty()
                                ? REQUIRED_LAST_NAME_ERR_MSG
                                : !isPhoneNumberNotEmpty()
                                        ? REQUIRED_PHONE_NUMBER_ERR_MSG
                                        : null);
        return response;
    }
}
