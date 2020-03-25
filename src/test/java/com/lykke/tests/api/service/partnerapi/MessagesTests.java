package com.lykke.tests.api.service.partnerapi;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.postMessage;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationExternalId;
import static com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.getMessage;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.partnerapi.model.SendMessageRequestModel;
import com.lykke.tests.api.service.partnerapi.model.SendMessageResponseModel;
import com.lykke.tests.api.service.partnerapi.model.SendMessageStatus;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateResponse;
import com.lykke.tests.api.service.partnersintegration.model.MessageGetResponseModel;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class MessagesTests extends BaseApiTest {

    private static final String MESSAGE_MEXT = "My message";
    private static final String SUBJECT_TEXT = "My subject";
    private static final String POS_ID = "posId";
    private static final String MESSAGE_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Message' must not be empty.";
    private static final String SUBJECT_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Subject' must not be empty.";
    private static final String CUSTOMER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Customer Id' must not be empty.";
    private static final String LOCATION_ID_MUST_NOT_BE_EMPTY = "'Location Id' must not be empty.";
    private static String partnerId;
    private static String partnerPassword;
    private static String partnerToken;
    private static String customerId;
    private static String customerPassword;
    private static String customerToken;
    private static String email;
    private static String phone;
    private static String locationExternalId;
    private static CustomerInfo customerInfo;
    private PartnerCreateResponse partnerData;

    static Stream<Arguments> getInvalidInputData() {
        return Stream.of(
                of(SendMessageRequestModel
                        .builder()
                        .customerId(getRandomUuid())
                        .locationId(locationExternalId)
                        .message(MESSAGE_MEXT)
                        .subject(SUBJECT_TEXT)
                        .posId(POS_ID)
                        .build(), SC_OK, SendMessageResponseModel
                        .builder()
                        .status(SendMessageStatus.CUSTOMER_NOT_FOUND)
                        .build()),
                of(SendMessageRequestModel
                        .builder()
                        .customerId(getRandomUuid())
                        .locationId(locationExternalId)
                        .message(MESSAGE_MEXT)
                        .subject(SUBJECT_TEXT)
                        .posId(POS_ID)
                        .build(), SC_OK, SendMessageResponseModel
                        .builder()
                        .status(SendMessageStatus.CUSTOMER_NOT_FOUND)
                        .build()),
                of(SendMessageRequestModel
                        .builder()
                        .locationId(locationExternalId)
                        .message(MESSAGE_MEXT)
                        .subject(SUBJECT_TEXT)
                        .posId(POS_ID)
                        .build(), SC_OK, SendMessageResponseModel
                        .builder()
                        // TODO: earlier itt was .status(SendMessageStatus.LOCATION_NOT_FOUND)
                        .status(SendMessageStatus.OK)
                        .build())
        );
    }

    static Stream<Arguments> getMissingInputData() {
        return Stream.of(
                of(SendMessageRequestModel
                        .builder()
                        .customerId(getRandomUuid())
                        .locationId(locationExternalId)
                        .posId(POS_ID)
                        .build(), SC_BAD_REQUEST, PartnerApiMessagesValidationErrorResponse
                        .builder()
                        .message(new String[]{MESSAGE_MUST_NOT_BE_EMPTY_ERROR_MESSAGE})
                        .subject(new String[]{SUBJECT_MUST_NOT_BE_EMPTY_ERROR_MESSAGE})
                        .locationId(new String[]{LOCATION_ID_MUST_NOT_BE_EMPTY})
                        .build()),
                of(SendMessageRequestModel
                        .builder()
                        .locationId(locationExternalId)
                        .message(MESSAGE_MEXT)
                        .subject(SUBJECT_TEXT)
                        .posId(POS_ID)
                        .build(), SC_BAD_REQUEST, PartnerApiMessagesValidationErrorResponse
                        .builder()
                        .customerId(new String[]{CUSTOMER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE})
                        .locationId(new String[]{LOCATION_ID_MUST_NOT_BE_EMPTY})
                        .build())
        );
    }

    @BeforeEach
    void setUp() {
        partnerPassword = generateValidPassword();
        email = generateRandomEmail();
        phone = FakerUtils.phoneNumber;
        customerPassword = generateValidPassword();
        customerInfo = registerDefaultVerifiedCustomer();
        customerId = customerInfo.getCustomerId();

        partnerId = getRandomUuid();
        partnerData = createDefaultPartner(partnerId, partnerPassword, generateRandomString(10),
                generateRandomString(10));
        locationExternalId = getLocationExternalId(partnerData);
        partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
    }

    @Test
    @UserStoryId(storyId = {2988, 4099})
    void shouldPostMessage() {
        val expectedSendMessageResponse = SendMessageResponseModel
                .builder()
                .status(SendMessageStatus.OK)
                .build();

        val expectedMessage = MessageGetResponseModel
                .builder()
                .customerId(customerId)
                .externalLocationId(locationExternalId)
                .message("My message")
                .partnerId(partnerData.getId())
                .subject("My subject")
                .posId("posId")
                .build();

        val actualSendMessageResponse = postMessage(SendMessageRequestModel
                .builder()
                .customerId(customerId)
                .locationId(locationExternalId)
                .message(MESSAGE_MEXT)
                .partnerId(partnerData.getId())
                .subject(SUBJECT_TEXT)
                .posId(POS_ID)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SendMessageResponseModel.class);

        assertEquals(expectedSendMessageResponse.getStatus(), actualSendMessageResponse.getStatus());

        val actualMessage = getMessage(actualSendMessageResponse.getPartnerMessageId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageGetResponseModel.class);

        assertAll(
                () -> assertEquals(expectedMessage.getCustomerId(), actualMessage.getCustomerId()),
                () -> assertEquals(expectedMessage.getExternalLocationId(), actualMessage.getExternalLocationId()),
                () -> assertEquals(expectedMessage.getMessage(), actualMessage.getMessage()),
                () -> assertEquals(expectedMessage.getSubject(), actualMessage.getSubject()),
                () -> assertEquals(expectedMessage.getPartnerId(), actualMessage.getPartnerId()),
                () -> assertEquals(expectedMessage.getPosId(), actualMessage.getPosId())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidInputData")
    @UserStoryId(storyId = {2988, 4099})
    void shouldNotPostMessageOnInvalidInput(SendMessageRequestModel requestModel, int status,
            SendMessageResponseModel expectedResult) {
        if (null == requestModel.getPartnerId()) {
            requestModel.setPartnerId(partnerData.getId());
        }
        if (null == requestModel.getCustomerId()) {
            requestModel.setCustomerId(customerId);
        }
        if (null == requestModel.getLocationId()) {
            requestModel.setLocationId(locationExternalId);
        }

        val actualResult = postMessage(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(SendMessageResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getMissingInputData")
    @UserStoryId(storyId = {2988, 4099})
    void shouldNotPostMessageOnInvalidInput(SendMessageRequestModel requestModel, int status,
            PartnerApiMessagesValidationErrorResponse expectedResult) {
        if (null == requestModel.getPartnerId()) {
            requestModel.setPartnerId(partnerData.getId());
        }

        val actualResult = postMessage(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(PartnerApiMessagesValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PartnerApiMessagesValidationErrorResponse {

        private String[] message;
        private String[] subject;
        private String[] customerId;
        private String[] locationId;
    }
}
