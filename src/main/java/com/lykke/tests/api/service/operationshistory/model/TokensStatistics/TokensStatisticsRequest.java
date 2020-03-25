package com.lykke.tests.api.service.operationshistory.model.TokensStatistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

@AllArgsConstructor
@Builder
@Data
public class TokensStatisticsRequest {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static String DATE_ERROR_MESSAGE = "The value '%s' is not valid.";
    private String dateTo;
    private String dateFrom;

    public String getToDate() {
        return String.valueOf(dateTo);
    }

    public String getFormDate() {
        return String.valueOf(dateFrom);
    }

    public int getHttpStatus() {
        return isFromDateValid() && isToDateValid()
                ? SC_OK
                : SC_BAD_REQUEST;
    }

    public ValidationErrorTokensResponse getValidationResponse() {
        val response = new ValidationErrorTokensResponse();
        response.getModelErrors().setDateFrom(isFromDateValid() ?  null : new String[]{ getFromDateValidationErrorMessage() });
        response.getModelErrors().setDateTo(isToDateValid() ?  null : new String[]{ getToDateValidationErrorMessage() });
        response.setErrorMessage(isToDateValid() ?  null : getToDateValidationErrorMessage() );
        return response;
    }

    private boolean isToDateValid() {
        return isDateFormatValid(dateTo);
    }

    private boolean isFromDateValid() {
        return isDateFormatValid(dateFrom);
    }

    private String getFromDateValidationErrorMessage() {
        return isDateFormatValid(dateFrom)
                ? EMPTY
                : String.format(DATE_ERROR_MESSAGE, dateFrom);
    }

    private String getToDateValidationErrorMessage() {
        return isDateFormatValid(dateTo)
                ? EMPTY
                : String.format(DATE_ERROR_MESSAGE, dateTo);
    }

    private boolean isDateFormatValid(String inputDate){
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
