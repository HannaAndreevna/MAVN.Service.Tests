package com.lykke.tests.api.service.credentials.model;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@NetClassName("none")
public class CredentialsErrorResponse {

    public static final String CLIENT_ID_ERROR_MESSAGE = "Client id required.";
    public static final String CLIENT_SECRET_EMPTY_ERROR_MESSAGE = "Password required";
    public static final String CLIENT_SECRET_ERROR_MESSAGE = "Password length should be between 8 and 50 characters. "
            + "Password should contain 1 lowercase, 1 uppercase, 1 digits and 1 special symbols. "
            + "Allowed special symbols are: !@#$%&. Whitespaces are allowed";
    public static final String PASSWORD_REQUIRED_ERROR_MESSAGE = "Password is a required field";
    public static final String EMAIL_ADDRESS_MESSAGE = "Email address is required";
    private String errorMessage;
    private ModelErrors modelErrors;

    @Builder(builderMethodName = "credentialsErrorBuilder")
    public CredentialsErrorResponse(String clientIdMessage, String clientSecretMessage) {
        val message = getMessage(clientIdMessage, clientSecretMessage);
        errorMessage = message;
        modelErrors = new ModelErrors();
        modelErrors.setClientId(getClientId(clientIdMessage));
        modelErrors.setClientSecret(getClientSecret(clientSecretMessage));
    }

    private String getMessage(String clientIdMessage, String clientSecretMessage) {
        return null == clientIdMessage || EMPTY.equalsIgnoreCase(clientIdMessage)
                ? clientSecretMessage
                : clientIdMessage;
    }

    private String[] getClientId(String clientIdMessage) {
        return null == clientIdMessage || EMPTY.equalsIgnoreCase(clientIdMessage) ? null
                : new String[]{clientIdMessage};
    }

    private String[] getClientSecret(String clientSecretMessage) {
        return null == clientSecretMessage || EMPTY.equalsIgnoreCase(clientSecretMessage) ? null
                : new String[]{clientSecretMessage};
    }
}
