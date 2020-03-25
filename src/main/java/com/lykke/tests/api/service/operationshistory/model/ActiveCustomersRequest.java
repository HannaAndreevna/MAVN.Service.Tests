package com.lykke.tests.api.service.operationshistory.model;

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
public class ActiveCustomersRequest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static String DATE_ERROR_MESSAGE = "The value '%s' is not valid.";
    private String toDate;
    private String fromDate;

    public String getPageSize() {
        return String.valueOf(toDate);
    }

    public String getCurrentPage() {
        return String.valueOf(fromDate);
    }

    public int getHttpStatus() {
        return isFromDateValid() && isToDateValid()
                ? SC_OK
                : SC_BAD_REQUEST;
    }

    public ValidationErrorResponse getValidationResponse() {
        val response = new ValidationErrorResponse();
        response.getModelErrors()
                .setFromDate(isFromDateValid() ? null : new String[]{getFromDateValidationErrorMessage()});
        response.getModelErrors().setToDate(isToDateValid() ? null : new String[]{getToDateValidationErrorMessage()});
        response.setErrorMessage(isToDateValid() ? null : getToDateValidationErrorMessage());
        return response;
    }

    private boolean isToDateValid() {
        return isDateFormatValid(toDate);
    }

    private boolean isFromDateValid() {
        return isDateFormatValid(fromDate);
    }

    private String getFromDateValidationErrorMessage() {
        return isDateFormatValid(fromDate)
                ? EMPTY
                : String.format(DATE_ERROR_MESSAGE, fromDate);
    }

    private String getToDateValidationErrorMessage() {
        return isDateFormatValid(toDate)
                ? EMPTY
                : String.format(DATE_ERROR_MESSAGE, toDate);
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
