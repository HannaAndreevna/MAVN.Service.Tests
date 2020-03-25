package com.lykke.tests.api.service.campaigns.model.burnrules;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.tests.api.common.enums.Localization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@AllArgsConstructor
@Builder
@Data
@NetClassName("?")
public class BurnRulesCreateRequest {

    private static final String RULE_CONTENT_TITLE_IS_MANDATORY_MESSAGE
            = "Rule Content 'Title' in English is mandatory";
    private static final String TITLE_MUST_NOT_BE_EMPTY = "'Title' must not be empty.";
    private static final String LENGTH_OF_TITLE_MUST_BE_AT_LEAST_3_MESSAGE =
            "The length of 'Title' must be at least 3 characters. You entered %s characters.";
    private static final String LENGTH_OF_TITLE_MUST_BE_50_CH_OR_FEWER_MESSAGE =
            "The length of 'Title' must be 50 characters or fewer. You entered %s characters.";
    private static final String CREATED_BY_CANNOT_BE_EMPTY_MESSAGE = "'Created By' must not be empty.";
    private static final String BR_YOU_SHOULD_HAVE_AT_LEAST_ONE_EN_CONTENT_MESSAGE =
            "You should have at least one English content";
    private static final String BR_YOU_SHOULD_HAVE_ONE_CONTENT_FOR_TYPE_MESSAGE =
            "Should have only one content for 'type' by localization";
    private static final String LENGTH_OF_DESCRIPTION_MUST_BE_AT_LEAST_3_MESSAGE =
            "The length of 'Description' must be at least 3 characters. You entered %s characters.";
    private static final String LENGTH_OF_DESCRIPTION_MUST_BE_1000_CH_OR_FEWER_MESSAGE =
            "The length of 'Description' must be 1000 characters or fewer. You entered %s characters.";
    private static final String AMOUNT_IN_TOKENS_MUST_BE_GR_THAN_0_MSG = "'Amount In Tokens' must be greater than '0'.";
    private static final String AMOUNT_IN_CURRENCY_MUST_BE_GR_THAN_0_MSG =
            "'Amount In Currency' must be greater than '0'.";

    private String createdBy;
    private BurnRuleContentCreateRequestModel[] burnRuleContents;
    private String title;
    private String description;
    private String value;
    private Localization localization;
    private String amountInTokens;
    private float amountInCurrency;
    private Vertical vertical;

    public int getHttpStatus() {
        return isCreatedByValid() && isTitleValid() && isDescriptionValid() && isBurnRuleContentValid()
                && isAmountInTokensValid() && isAmountInCurrencyValid()
                ? SC_OK
                : SC_BAD_REQUEST;
    }

    public ValidationErrorBurnRuleResponse getValidationResponse() {
        val response = new ValidationErrorBurnRuleResponse();
        response.getModelErrors()
                .setTitle(isTitleValid() ? null : getTitleValidationErrorMessage());
        response.getModelErrors()
                .setDescription(isDescriptionValid() ? null : getDescriptionValidationErrorMessage());
        response.getModelErrors()
                .setCreatedBy(isCreatedByValid() ? null : new String[]{CREATED_BY_CANNOT_BE_EMPTY_MESSAGE});
        response.getModelErrors()
                .setAmountInTokens(
                        isAmountInTokensValid() ? null : new String[]{AMOUNT_IN_TOKENS_MUST_BE_GR_THAN_0_MSG});
        response.getModelErrors()
                .setAmountInCurrency(
                        isAmountInCurrencyValid() ? null : new String[]{AMOUNT_IN_CURRENCY_MUST_BE_GR_THAN_0_MSG});
        response.setErrorMessage(getErrorMessage());
        return response;
    }

    private boolean isFieldValueValid(String value, int min, int max) {
        return value.length() >= min && value.length() <= max;
    }

    private boolean isCreatedByValid() {
        return !(createdBy.length() == 0 && createdBy.isEmpty());
    }

    private boolean isTitleValid() {
        return isFieldValueValid(title, 3, 50);
    }

    private boolean isDescriptionValid() {
        return isFieldValueValid(description, 3, 1000);
    }

    private boolean isContentTitleValueValid() {
        return isFieldValueValid(burnRuleContents[0].getValue(), 3, 50);
    }

    private boolean isContentTitleLocalizationValueValid() {
        return localization == Localization.EN;
    }

    private boolean isBurnRuleContentValid() {
        return burnRuleContents.length > 0 && isContentTitleValueValid() && isContentTitleLocalizationValueValid();
    }

    private boolean isAmountInTokensValid() {
        return (Double.valueOf(amountInTokens) > 0) && (Double.valueOf(amountInTokens) < 2147483647);
    }

    private boolean isAmountInCurrencyValid() {
        return (amountInCurrency > 0) && (Double.valueOf(amountInTokens) < 2147483647);
    }

    private String getErrorMessage() {
        String errorMessage;
        if (!isTitleValid() && title.length() == 0) {
            errorMessage = TITLE_MUST_NOT_BE_EMPTY;
        } else if (!isTitleValid() && title.length() < 3) {
            errorMessage = String.format(LENGTH_OF_TITLE_MUST_BE_AT_LEAST_3_MESSAGE, 2);
        } else if (!isTitleValid() && title.length() > 50) {
            errorMessage = String.format(LENGTH_OF_TITLE_MUST_BE_50_CH_OR_FEWER_MESSAGE, 51);
        } else if (!isDescriptionValid() && description.length() < 3) {
            errorMessage = String.format(LENGTH_OF_DESCRIPTION_MUST_BE_AT_LEAST_3_MESSAGE, 2);
        } else if (!isDescriptionValid() && description.length() > 1000) {
            errorMessage = String.format(LENGTH_OF_DESCRIPTION_MUST_BE_1000_CH_OR_FEWER_MESSAGE, 1001);
        } else if (!isAmountInTokensValid()) {
            errorMessage = AMOUNT_IN_TOKENS_MUST_BE_GR_THAN_0_MSG;
        } else if (!isAmountInCurrencyValid()) {
            errorMessage = AMOUNT_IN_CURRENCY_MUST_BE_GR_THAN_0_MSG;
        } else {
            errorMessage = CREATED_BY_CANNOT_BE_EMPTY_MESSAGE;
        }

        return errorMessage;
    }

    private String[] getTitleValidationErrorMessage() {
        return (!isTitleValid() && title.length() == 0)
                ? new String[]{TITLE_MUST_NOT_BE_EMPTY, String.format(LENGTH_OF_TITLE_MUST_BE_AT_LEAST_3_MESSAGE, 0)}
                : (title.length() < 3)
                        ? new String[]{String.format(LENGTH_OF_TITLE_MUST_BE_AT_LEAST_3_MESSAGE, 2)}
                        : new String[]{String.format(LENGTH_OF_TITLE_MUST_BE_50_CH_OR_FEWER_MESSAGE, 51)};
    }

    private String[] getDescriptionValidationErrorMessage() {
        return (!isDescriptionValid() && description.length() < 3)
                ? new String[]{String.format(LENGTH_OF_DESCRIPTION_MUST_BE_AT_LEAST_3_MESSAGE, 2)}
                : new String[]{String.format(LENGTH_OF_DESCRIPTION_MUST_BE_1000_CH_OR_FEWER_MESSAGE, 1001)};
    }
}
