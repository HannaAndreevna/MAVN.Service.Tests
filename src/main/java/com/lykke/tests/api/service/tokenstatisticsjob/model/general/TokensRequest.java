package com.lykke.tests.api.service.tokenstatisticsjob.model.general;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Builder
@Data
public class TokensRequest {

    private static final String DATE_FORMAT_REG_EX = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))";
    private static final String INVALID_DATE_ERR_MSG = "The value '%s' is not valid.";
    private static final String EMPTY_DATE_ERR_MSG = "The value '%s' is invalid.";
    String date;

    private boolean isDateFormatValid() {
        return date.matches(DATE_FORMAT_REG_EX);
    }

    public ValidationErrorResponseModel getValidationErrorResponse() {
        val template = StringUtils.EMPTY == date
                ? EMPTY_DATE_ERR_MSG
                : INVALID_DATE_ERR_MSG;
        val formattedInvalidDateErrMsg = String.format(template, date);
        val response = new ValidationErrorResponseModel();
        response.setErrorMessage(isDateFormatValid()
                ? null
                : formattedInvalidDateErrMsg);
        response.setModelErrors(isDateFormatValid()
                ? null
                : ModelErrorsModel
                        .builder()
                        .dt(new String[]{formattedInvalidDateErrMsg})
                        .build());
        return response;
    }
}
