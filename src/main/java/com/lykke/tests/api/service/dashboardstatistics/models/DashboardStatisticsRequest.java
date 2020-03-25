package com.lykke.tests.api.service.dashboardstatistics.models;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.QueryParameters;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;

@AllArgsConstructor
@Builder
@Data
@NetClassName("LeadsListRequestModel")
// also: TokensListRequestModel
@QueryParameters
public class DashboardStatisticsRequest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final String DATE_ERROR_MESSAGE = "The value '%s' is not valid for %s.";
    private static final String DATE_SH_BE_BEFORE_MSG = "%s should be before or equal to %s";
    private static final String IS_REQUIRED_MSG = "%s is required";
    private static final String TO_DATE_MB_EQ_OR_LT_FROM_DATE_MSG = "ToDate must be equal or later than FromDate.";
    private static final String TO_DATE_MB_EQ_OR_ER_THAN_TODAY_MSG = "ToDate must be equal or earlier than today.";

    private static String currentDate = Instant.now().toString().split("T")[0];

    private String toDate;
    private String fromDate;

    public String getToDate() {
        return String.valueOf(toDate);
    }

    public String getFromDate() {
        return String.valueOf(fromDate);
    }

    private boolean isOrderOfDatesCorrect() {
        return isDateBeforeOrEqualToOtherDate(fromDate, toDate)
                && isDateBeforeOrEqualToOtherDate(toDate, currentDate);
    }

    public int getHttpStatus() {
        return (isFromDateValid() && isToDateValid() && isOrderOfDatesCorrect())
                ? SC_OK
                : SC_BAD_REQUEST;
    }

    @SneakyThrows
    private boolean isDateBeforeOrEqualToOtherDate(String date1, String date2) {

        val d1 = DATE_FORMAT.parse(date1);
        val d2 = DATE_FORMAT.parse(date2);

        return d1.compareTo(d2) <= 0;
    }

    //TODO:Update to cover the model errors, i.e:
    //"ModelErrors": {
    //    "ToDate.Date": [
    //    "ToDate must be equal or later than FromDate."
    //]
    public ValidationErrorDashboardStatisticsResponse getValidationResponse() {
        val response = new ValidationErrorDashboardStatisticsResponse();
        response.getModelErrors().setFromDate(getFromDateValidationErrorMessage());
        response.getModelErrors().setToDate(getToDateValidationErrorMessage());
        response.setErrorMessage(getErrorMessage());
        return response;
    }

    private boolean isToDateValid() {
        return isDateFormatValid(toDate);
    }

    private boolean isFromDateValid() {
        return isDateFormatValid(fromDate);
    }

    private String[] getFromDateValidationErrorMessage() {
        return (!isFromDateValid() && !fromDate.isEmpty())
                ? new String[]{String.format(DATE_ERROR_MESSAGE, fromDate, "FromDate")}
                : null;
    }

    private String[] getToDateValidationErrorMessage() {
        return (!isToDateValid() && !toDate.isEmpty())
                ? new String[]{String.format(DATE_ERROR_MESSAGE, toDate, "ToDate")}
                : null;
    }

    private String getErrorMessage() {
        String errorMessage;
        if (fromDate.isEmpty()) {
            errorMessage = String.format(IS_REQUIRED_MSG, "FromDate");
        } else if (!isFromDateValid()) {
            errorMessage = String.format(DATE_ERROR_MESSAGE, fromDate, "FromDate");
        } else if (toDate.isEmpty()) {
            errorMessage = String.format(IS_REQUIRED_MSG, "ToDate");
        } else if (isToDateValid() && isFromDateValid()
                && isDateBeforeOrEqualToOtherDate(fromDate, toDate)
                && !isDateBeforeOrEqualToOtherDate(toDate, currentDate)) {
            errorMessage = TO_DATE_MB_EQ_OR_ER_THAN_TODAY_MSG;
        } else if (isToDateValid() && isFromDateValid()
                && !isDateBeforeOrEqualToOtherDate(fromDate, toDate)
                && isDateBeforeOrEqualToOtherDate(toDate, currentDate)) {
            errorMessage = TO_DATE_MB_EQ_OR_LT_FROM_DATE_MSG;
        } else if (!isToDateValid()) {
            errorMessage = String.format(DATE_ERROR_MESSAGE, toDate, "ToDate");
        } else {
            errorMessage = null;
        }

        return errorMessage;
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
