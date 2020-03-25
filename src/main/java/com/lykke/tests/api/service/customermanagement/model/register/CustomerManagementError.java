package com.lykke.tests.api.service.customermanagement.model.register;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@PublicApi
public enum CustomerManagementError {
    NONE("None", "No errors"),
    LOGIN_NOT_FOUND("LoginNotFound", "Credentials are not existing for such login"),
    PASSWORD_MISMATCH("PasswordMismatch", "Password mismatch"),
    REGISTERED_WITH_ANOTHER_PASSWORD("RegisteredWithAnotherPassword", "Password does not match"),
    ALREADY_REGISTERED("AlreadyRegistered",
            "This error is returned in case that we have unfinished registration with created credentials but the password is different"),
    INVALID_LOGIN_FORMAT("InvalidLoginFormat", "The login field does not match the expected format"),
    INVALID_PASSWORD_FORMAT("InvalidPasswordFormat", "The password field does not match the expected format"),
    LOGIN_EXISTS_WITH_DIFFERENT_PROVIDER("LoginExistsWithDifferentProvider",
            "There is already created profile but it is using another provider"),
    ALREADY_REGISTERED_WITH_GOOGLE("AlreadyRegisteredWithGoogle",
            "There is already created profile using Google registration"),
    CUSTOMER_BLOCKED("CustomerBlocked", "Customer is blocked"),
    INVALID_COUNTRY_OF_NATIONALITY_ID("InvalidCountryOfNationalityId",
            "The provider country identifier does not match any of our countries");

    private static Map<String, CustomerManagementError> FORMAT_MAP =
            Stream.of(CustomerManagementError.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static CustomerManagementError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
