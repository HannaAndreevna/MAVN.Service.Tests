package com.lykke.tests.api.service.campaigns.model.mobile;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@AllArgsConstructor
@Builder
@Data
public class MobileGetByIdRequest {

    private static final String ID_IS_NOT_VALID_MSG = "The value \'%s\' is not valid.";
    private static String uuidRegEx =
            "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}";

    private String burnRuleId;
    private String earnRuleId;

    private Boolean isBurnRuleIdValid() {
        return burnRuleId.matches(uuidRegEx);
    }

    private Boolean isEarnRuleIdValid() {
        return earnRuleId.matches(uuidRegEx);
    }

    private String getErrorMessage() {
        return !isBurnRuleIdValid()
                ? String.format(ID_IS_NOT_VALID_MSG, burnRuleId)
                : (!isEarnRuleIdValid()
                        ? String.format(ID_IS_NOT_VALID_MSG, earnRuleId)
                        : null);
    }

    private String[] getBrErrorMessage() {
        String[] message = null;
        if (null != burnRuleId) {
            message = isBurnRuleIdValid()
                    ? null
                    : new String[]{String.format(ID_IS_NOT_VALID_MSG, burnRuleId)};
        }

        return message;
    }

    private String[] getErErrorMessage() {
        String[] message = null;
        if (null != earnRuleId) {
            message = isEarnRuleIdValid()
                    ? null
                    : new String[]{String.format(ID_IS_NOT_VALID_MSG, earnRuleId)};
        }

        return message;
    }

    public ValidationResponse getValidationResponse() {
        val response = new ValidationResponse();
        response.getModelErrors().setBurnRuleId(getBrErrorMessage());
        response.getModelErrors().setEarnRuleId(getErErrorMessage());
        response.setErrorMessage(getErrorMessage());
        return response;
    }

    public int getHttpStatus() {
        return (isBurnRuleIdValid() && isEarnRuleIdValid())
                ? SC_NO_CONTENT
                : SC_BAD_REQUEST;
    }
}
