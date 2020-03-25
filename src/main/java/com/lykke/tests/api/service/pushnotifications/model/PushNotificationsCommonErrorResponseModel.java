package com.lykke.tests.api.service.pushnotifications.model;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushNotificationsCommonErrorResponseModel {

    private static final String APPLE_TOKEN_MUST_NOT_BE_EMPTY = "'Apple Token' must not be empty.";
    private static final String FIREBASE_TOKEN_MUST_NOT_BE_EMPTY = "'Firebase Token' must not be empty.";

    @JsonProperty("AppleToken")
    private String appleToken;
    @JsonProperty("FirebaseToken")
    private String firebaseToken;

    public String[] getAppleTokenMessage() {
        return EMPTY.equals(appleToken)
                ? new String[]{APPLE_TOKEN_MUST_NOT_BE_EMPTY}
                : new String[]{EMPTY};
    }

    public String[] getFirebaseTokenMessage() {
        return EMPTY.equals(firebaseToken)
                ? new String[]{FIREBASE_TOKEN_MUST_NOT_BE_EMPTY}
                : new String[]{EMPTY};
    }
}
