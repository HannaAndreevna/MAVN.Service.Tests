package com.lykke.tests.api.service.admin.model.burnrules;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.tests.api.common.enums.BusinessVertical;
import com.lykke.tests.api.common.enums.Localization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@AllArgsConstructor
@Data
@Builder
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@NetClassName("none")
public class BurnRuleCreateRequest {

    private static final String TITLE_MUST_NOT_BE_EMPTY = "'Title' must not be empty.";
    private static final String LENGTH_OF_TITLE_MUST_BE_AT_LEAST_3_MESSAGE =
            "The length of 'Title' must be at least 3 characters. You entered %s characters.";
    private static final String LENGTH_OF_TITLE_MUST_BE_50_CH_OR_FEWER_MESSAGE =
            "Title should be specified with length between 3 and 50 characters";
    private static final String LENGTH_OF_DESCRIPTION_VALIDATION_MSG =
            "Description should be specified with length between 3 and 1000 characters";
    private static final String AMOUNT_IN_TOKENS_IS_REQUIRED_MSG = "Amount in tokens required.";
    private static final String AMOUNT_IN_CURRENCY_IS_REQUIRED_MSG =
            "Amount in currency required.";
    private static final String BUSINESS_VERTICAL_REQUIRED_MSG = "Business vertical required";

    private String title;
    private String description;
    private String amountInTokens;
    private float amountInCurrency;
    private BusinessVertical businessVertical;
    private MobileContentCreateRequest[] mobileContents;

    public int getHttpStatus() {
        return isTitleValid() && isDescriptionValid() && isBurnRuleContentValid()
                && isAmountInTokensValid() && isAmountInCurrencyValid() && isBusinessVerticalProvided()
                ? SC_OK
                : SC_BAD_REQUEST;
    }

    public ValidationErrorResponse getValidationResponse() {
        val response = new ValidationErrorResponse();
        response.setTitle(isTitleValid() ? null : getTitleValidationErrorMessage());
        response.setDescription(isDescriptionValid() ? null : getDescriptionValidationErrorMessage());
        response.setAmountInTokens(
                isAmountInTokensValid() ? null : new String[]{AMOUNT_IN_TOKENS_IS_REQUIRED_MSG});
        response.setAmountInCurrency(
                isAmountInCurrencyValid() ? null : new String[]{AMOUNT_IN_CURRENCY_IS_REQUIRED_MSG});
        response.setBusinessVertical(
                isBusinessVerticalProvided() ? null : new String[]{BUSINESS_VERTICAL_REQUIRED_MSG});
        return response;
    }

    private boolean isBusinessVerticalProvided() {
        return null != businessVertical;
    }

    private boolean isFieldValueValid(String value, int min, int max) {
        return value.length() >= min && value.length() <= max;
    }

    private boolean isTitleValid() {
        return isFieldValueValid(title, 3, 50);
    }

    private boolean isDescriptionValid() {
        return isFieldValueValid(description, 3, 1000);
    }

    private boolean isContentTitleValueValid() {
        return isFieldValueValid(mobileContents[0]
                .getDescription(), 3, 50);
    }

    private boolean isContentTitleLocalizationValueValid() {
        return mobileContents[0].getMobileLanguage() == Localization.EN;
    }

    private boolean isBurnRuleContentValid() {
        return mobileContents.length > 0 && isContentTitleValueValid() && isContentTitleLocalizationValueValid();
    }

    private boolean isAmountInTokensValid() {
        return (Double.valueOf(amountInTokens) > 0) && (Double.valueOf(amountInTokens) < 2147483647);
    }

    private boolean isAmountInCurrencyValid() {
        return (amountInCurrency > 0) && (Double.valueOf(amountInTokens) < 2147483647);
    }

    private String[] getTitleValidationErrorMessage() {
        return (!isTitleValid() && title.length() == 0)
                ? new String[]{TITLE_MUST_NOT_BE_EMPTY}
                : (title.length() < 3)
                        ? new String[]{String.format(LENGTH_OF_TITLE_MUST_BE_AT_LEAST_3_MESSAGE, 2)}
                        : new String[]{String.format(LENGTH_OF_TITLE_MUST_BE_50_CH_OR_FEWER_MESSAGE, 51)};
    }

    private String[] getDescriptionValidationErrorMessage() {
        return (!isDescriptionValid() && description.length() < 3)
                ? new String[]{String.format(LENGTH_OF_DESCRIPTION_VALIDATION_MSG, 2)}
                : new String[]{String.format(LENGTH_OF_DESCRIPTION_VALIDATION_MSG, 1001)};
    }
}
