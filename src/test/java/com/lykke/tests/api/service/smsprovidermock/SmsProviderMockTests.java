package com.lykke.tests.api.service.smsprovidermock;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.service.smsprovidermock.SmsProviderMockUtils.deleteSms;
import static com.lykke.tests.api.service.smsprovidermock.SmsProviderMockUtils.getSentSms;
import static com.lykke.tests.api.service.smsprovidermock.SmsProviderMockUtils.getSentSmsDetails;
import static com.lykke.tests.api.service.smsprovidermock.SmsProviderMockUtils.querySms;
import static com.lykke.tests.api.service.smsprovidermock.SmsProviderMockUtils.sendSms;
import static io.restassured.http.ContentType.HTML;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.smsprovidermock.model.PaginatedSmsRequestModel;
import com.lykke.tests.api.service.smsprovidermock.model.PaginatedSmsResponseModel;
import com.lykke.tests.api.service.smsprovidermock.model.SendSmsRequestModel;
import com.lykke.tests.api.service.smsprovidermock.model.SmsResponseModel;
import lombok.val;
import org.junit.jupiter.api.Test;

public class SmsProviderMockTests extends BaseApiTest {

    private static final int SMS_LENGTH = 140;
    private static final int CURRENT_PAGE = 1;
    private static final int PAGE_SIZE = 500;
    private static final int TOTAL_PAGE_COUNT = 1;

    @Test
    void shouldSendSms() {
        val messageId = getRandomUuid();
        val message = generateRandomString(SMS_LENGTH);
        val phoneNumber = FakerUtils.phoneNumber;
        val expectedResult = PaginatedSmsResponseModel
                .builder()
                .currentPage(CURRENT_PAGE)
                .totalPageCount(TOTAL_PAGE_COUNT)
                .sms(new SmsResponseModel[]{
                        SmsResponseModel
                                .builder()
                                .messageId(messageId)
                                .message(message)
                                .phoneNumber(phoneNumber)
                                .build()
                })
                .build();

        sendSms(SendSmsRequestModel
                .builder()
                .messageId(messageId)
                .message(message)
                .phoneNumber(phoneNumber)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val actualResult = querySms(PaginatedSmsRequestModel
                .builder()
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .messageId(messageId)
                .phoneNumber(phoneNumber)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedSmsResponseModel.class);

        val expectedSms = expectedResult.getSms()[0];
        val actualSms = actualResult.getSms()[0];
        assertAll(
                () -> assertEquals(expectedResult.getCurrentPage(), actualResult.getCurrentPage()),
                () -> assertEquals(expectedResult.getTotalPageCount(), actualResult.getTotalPageCount()),
                () -> assertEquals(expectedSms.getMessageId(), actualSms.getMessageId()),
                () -> assertEquals(expectedSms.getMessage(), actualSms.getMessage()),
                () -> assertEquals(expectedSms.getPhoneNumber(), actualSms.getPhoneNumber())
        );
    }

    @Test
    void shouldDeleteSms() {
        val messageId = getRandomUuid();
        val message = generateRandomString(SMS_LENGTH);
        val phoneNumber = FakerUtils.phoneNumber;

        sendSms(SendSmsRequestModel
                .builder()
                .messageId(messageId)
                .message(message)
                .phoneNumber(phoneNumber)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        deleteSms(messageId)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualResult = querySms(PaginatedSmsRequestModel
                .builder()
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .messageId(messageId)
                .phoneNumber(phoneNumber)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedSmsResponseModel.class);

        assertAll(
                () -> assertEquals(CURRENT_PAGE, actualResult.getCurrentPage()),
                () -> assertEquals(0, actualResult.getTotalPageCount()),
                () -> assertEquals(0, actualResult.getSms().length)
        );
    }

    @Test
    void shouldReturnAllSms() {
        val messageId = getRandomUuid();
        val message = generateRandomString(SMS_LENGTH);
        val phoneNumber = FakerUtils.phoneNumber;

        sendSms(SendSmsRequestModel
                .builder()
                .messageId(messageId)
                .message(message)
                .phoneNumber(phoneNumber)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val actualResult = getSentSms()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .contentType(HTML)
                .extract().asString();

        assertAll(
                () -> assertTrue(actualResult.contains(messageId)),
                () -> assertTrue(actualResult.contains(message)),
                () -> assertTrue(actualResult.contains(phoneNumber))
        );
    }

    @Test
    void shouldReturnSmsDetails() {
        val messageId = getRandomUuid();
        val message = generateRandomString(SMS_LENGTH);
        val phoneNumber = FakerUtils.phoneNumber;

        sendSms(SendSmsRequestModel
                .builder()
                .messageId(messageId)
                .message(message)
                .phoneNumber(phoneNumber)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val actualResult = getSentSmsDetails(messageId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .contentType(HTML)
                .extract().asString();

        assertAll(
                () -> assertTrue(actualResult.contains(messageId)),
                () -> assertTrue(actualResult.contains(message)),
                () -> assertTrue(actualResult.contains(phoneNumber))
        );
    }
}
