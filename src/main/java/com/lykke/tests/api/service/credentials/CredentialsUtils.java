package com.lykke.tests.api.service.credentials;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.PathConsts.CredentialsEndpoint.PASSWORD_RESET_PATH;
import static com.lykke.tests.api.base.PathConsts.CredentialsEndpoint.RESET_IDENTIFIER_PATH;
import static com.lykke.tests.api.base.PathConsts.getFullPath;
import static com.lykke.tests.api.base.Paths.Credentials.CREDENTIALS_API_PATH;
import static com.lykke.tests.api.base.Paths.Credentials.CREDENTIALS_CLIENT_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.Credentials.CREDENTIALS_CLIENT_SECRET_API_PATH;
import static com.lykke.tests.api.base.Paths.Credentials.PARTNERS_API_PATH;
import static com.lykke.tests.api.base.Paths.Credentials.PARTNERS_VALIDATE_API_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.service.credentials.model.CredentialsCreateRequest;
import com.lykke.tests.api.service.credentials.model.CredentialsCreateResponse;
import com.lykke.tests.api.service.credentials.model.CredentialsUpdateRequest;
import com.lykke.tests.api.service.credentials.model.GenerateClientIdRequest;
import com.lykke.tests.api.service.credentials.model.GenerateClientSecretRequest;
import com.lykke.tests.api.service.credentials.model.PartnerCredentialsCreateRequest;
import com.lykke.tests.api.service.credentials.model.PartnerCredentialsRemoveRequest;
import com.lykke.tests.api.service.credentials.model.PartnerCredentialsUpdateRequest;
import com.lykke.tests.api.service.credentials.model.PartnerCredentialsValidationRequest;
import com.lykke.tests.api.service.credentials.model.PasswordResetRequest;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CredentialsUtils {

    public Response resetCustomerIdentifier(String customerId) {
        return getHeader()
                .get(CREDENTIALS_API_PATH + getFullPath(RESET_IDENTIFIER_PATH.getPath(), customerId));
    }

    Response resetPassword(String customerEmail, String resetIdentifier, String newPassword) {
        return getHeader()
                .body(PasswordResetRequest
                        .builder()
                        .customerEmail(customerEmail)
                        .resetIdentifier(resetIdentifier)
                        .password(newPassword)
                        .build())
                .post(CREDENTIALS_API_PATH + PASSWORD_RESET_PATH.getPath());
    }

    public Response createCredentials(String customerEmail, String password, String customerId) {
        return getHeader()
                .body(CredentialsCreateRequest
                        .credentialsUpdateRequestBuilder()
                        .clientId(customerEmail)
                        .clientSecret(password)
                        .customerId(customerId)
                        .build())
                .post(CREDENTIALS_API_PATH);
    }

    public Response updateCredentials(String customerEmail, String password, String customerId) {
        return getHeader()
                .body(CredentialsUpdateRequest
                        .credentialsUpdateRequestBuilder()
                        .clientId(customerEmail)
                        .clientSecret(password)
                        .customerId(customerId)
                        .build())
                .put(CREDENTIALS_API_PATH);
    }

    public Response createPartnerCredentials(PartnerCredentialsCreateRequest requestObject) {
        return getHeader()
                .body(requestObject)
                .post(PARTNERS_API_PATH)
                .thenReturn();
    }

    public CredentialsCreateResponse createPartnerCredentials(String partnerId, String partnerPassword,
            String clientId) {
        return createPartnerCredentials(PartnerCredentialsCreateRequest
                .credentialsCreateRequestBuilder()
                .clientId(clientId)
                .clientSecret(partnerPassword)
                .partnerId(partnerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CredentialsCreateResponse.class);
    }

    public Response removePartnerCredentials(PartnerCredentialsRemoveRequest requestObject) {
        return getHeader()
                .queryParams(getQueryParams(requestObject))
                .delete(PARTNERS_API_PATH)
                .thenReturn();
    }

    public Response updatePartnerCredentials(PartnerCredentialsUpdateRequest requestObject) {
        return getHeader()
                .body(requestObject)
                .put(PARTNERS_API_PATH)
                .thenReturn();
    }

    public Response validatePartnerCredentials(PartnerCredentialsValidationRequest requestObject) {
        return getHeader()
                .body(requestObject)
                .post(PARTNERS_VALIDATE_API_PATH)
                .thenReturn();
    }

    public Response generateClientId(GenerateClientIdRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(CREDENTIALS_CLIENT_ID_API_PATH)
                .thenReturn();
    }

    public Response generateClientSecret(GenerateClientSecretRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(CREDENTIALS_CLIENT_SECRET_API_PATH)
                .thenReturn();
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ValidationErrorResponse {

        private String errorMessage;
        private ModelErrors modelErrors;

        @AllArgsConstructor
        @Builder
        @Data
        @NoArgsConstructor
        @JsonNaming(UpperCamelCaseStrategy.class)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ModelErrors {

            private String[] length;
        }
    }
}
