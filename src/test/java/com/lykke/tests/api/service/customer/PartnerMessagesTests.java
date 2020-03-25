package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.PartnersMessagesUtils.getPartnerMessageById;
import static com.lykke.tests.api.service.customer.PartnersMessagesUtils.getPartnerMessageById_Deprecated;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.generateClientId;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.generateClientSecret;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationExternalId;
import static com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.postMessage;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customer.model.partnersmessages.PartnerMessagesResponseModel;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateResponse;
import com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.ValidationErrorResponse;
import com.lykke.tests.api.service.partnersintegration.model.MessagesPostRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.MessagesPostResponseModel;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PartnerMessagesTests extends BaseApiTest {

    private static final String AUTOMATION_SUBJECT = "TEST AUTOMATION SUBJECT";
    private static final String THE_EXTERNAL_LOCATION_ID_FIELD_IS_REQUIRED = "The ExternalLocationId field is required.";

    private static String partnerId;
    private static String password;
    private static String customerId;
    private static String newPartnerId;
    private static String partnerExternalLocationId;
    private static String customerToken;
    private PartnerCreateResponse partnerData;

    @BeforeEach
    void setUp() {
        var customer = new RegistrationRequestModel();
        customerId = registerCustomer(customer);
        customerToken = getUserToken(customer);

        partnerId = generateClientId();
        password = generateClientSecret();
        partnerData = createDefaultPartner(partnerId, password, generateRandomString(10),
                generateRandomString(10));
        newPartnerId = partnerData.getId();
        partnerExternalLocationId = getLocationExternalId(partnerData);
    }

    @Test
    @UserStoryId(storyId = 2760)
    void shouldGetPartnerMessageById_Deprecated() {

        val partnerMessageRequestObject = MessagesPostRequestModel.builder()
                .partnerId(newPartnerId)
                .customerId(customerId)
                .subject(AUTOMATION_SUBJECT)
                .message(AUTOMATION_SUBJECT)
                .externalLocationId(partnerExternalLocationId)
                .sendPushNotification(true)
                .build();

        val postMessageResult = postMessage(partnerMessageRequestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessagesPostResponseModel.class);

        val getPartnerMessageResult = getPartnerMessageById_Deprecated(postMessageResult.getPartnerMessageId(),
                customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerMessagesResponseModel.class);

        assertAll(
                () -> assertEquals(postMessageResult.getPartnerMessageId(),
                        getPartnerMessageResult.getPartnerMessageId()),
                () -> assertEquals(partnerMessageRequestObject.getPartnerId(), getPartnerMessageResult.getPartnerId()),
                () -> assertEquals(partnerMessageRequestObject.getCustomerId(),
                        getPartnerMessageResult.getCustomerId()),
                () -> assertEquals(partnerMessageRequestObject.getSubject(), getPartnerMessageResult.getSubject()),
                () -> assertEquals(partnerMessageRequestObject.getMessage(), getPartnerMessageResult.getMessage())
        );
    }

    @Test
    @UserStoryId(3815)
    void shouldGetPartnerMessageById() {

        val partnerMessageRequestObject = MessagesPostRequestModel.builder()
                .partnerId(newPartnerId)
                .customerId(customerId)
                .subject(AUTOMATION_SUBJECT)
                .message(AUTOMATION_SUBJECT)
                .externalLocationId(partnerExternalLocationId)
                .sendPushNotification(true)
                .build();

        val postMessageResult = postMessage(partnerMessageRequestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessagesPostResponseModel.class);

        val getPartnerMessageResult = getPartnerMessageById(postMessageResult.getPartnerMessageId(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerMessagesResponseModel.class);

        assertAll(
                () -> assertEquals(postMessageResult.getPartnerMessageId(),
                        getPartnerMessageResult.getPartnerMessageId()),
                () -> assertEquals(partnerMessageRequestObject.getPartnerId(), getPartnerMessageResult.getPartnerId()),
                () -> assertEquals(partnerMessageRequestObject.getCustomerId(),
                        getPartnerMessageResult.getCustomerId()),
                () -> assertEquals(partnerMessageRequestObject.getSubject(), getPartnerMessageResult.getSubject()),
                () -> assertEquals(partnerMessageRequestObject.getMessage(), getPartnerMessageResult.getMessage())
        );
    }

    @Test
    @UserStoryId(4098)
    void shouldNotPostPartnerMessageWithoutLocationId() {

        val expectedResult = ValidationErrorResponse
                .builder()
                .externalLocationId(new String[]{THE_EXTERNAL_LOCATION_ID_FIELD_IS_REQUIRED})
                .build();

        val partnerMessageRequestObject = MessagesPostRequestModel
                .builder()
                .partnerId(newPartnerId)
                .customerId(customerId)
                .subject(AUTOMATION_SUBJECT)
                .message(AUTOMATION_SUBJECT)
                .sendPushNotification(true)
                .build();

        val actualResult = postMessage(partnerMessageRequestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }
}
