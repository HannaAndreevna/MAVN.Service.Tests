package com.lykke.tests.api.service.customerprofile.statistics.model;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@AllArgsConstructor
@Builder
@Data
public class CustomerStatiscticsRequest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static String DATE_ERROR_MESSAGE = "The value '%s' is not valid.";
    private String errorMessage;
    private String startDate;
    private String endDate;

    public String getStartDate() {
        return String.valueOf(startDate);
    }

    public String getEndDate() {
        return String.valueOf(endDate);
    }

    public int getHttpStatus() {
        return isStartDateValid() && isEndDateValid()
                ? SC_OK
                : SC_BAD_REQUEST;
    }

    public ValidationErrorResponse getValidationResponse() {
        val response = new ValidationErrorResponse();
        val modelErrors = new ModelErrors();
        modelErrors.setStartDate(isStartDateValid() ? null : new String[]{getFromDateValidationErrorMessage()});
        modelErrors.setEndDate(isEndDateValid() ? null : new String[]{getToDateValidationErrorMessage()});
        response.setModelErrors(modelErrors);
        response.setErrorMessage(null == response.getModelErrors().getEndDate()
                ? response.getModelErrors().getStartDate()[0]
                : response.getModelErrors().getEndDate()[0]);
        return response;
    }

    private boolean isEndDateValid() {
        return isDateFormatValid(endDate);
    }

    private boolean isStartDateValid() {
        return isDateFormatValid(startDate);
    }

    private String getFromDateValidationErrorMessage() {
        return isDateFormatValid(startDate)
                ? EMPTY
                : String.format(DATE_ERROR_MESSAGE, startDate);
    }

    private String getToDateValidationErrorMessage() {
        return isDateFormatValid(endDate)
                ? EMPTY
                : String.format(DATE_ERROR_MESSAGE, endDate);
    }

    private boolean isDateFormatValid(String inputDate) {
        if (inputDate != null) {
            try {
                DATE_FORMAT.parse(inputDate);

            } catch (ParseException e) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
}
