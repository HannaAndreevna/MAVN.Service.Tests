package com.lykke.tests.api.service.smsprovidermock;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.SmsProviderMock.SEND_SMS_API_PATH;
import static com.lykke.tests.api.base.Paths.SmsProviderMock.SENT_SMS_API_PATH;
import static com.lykke.tests.api.base.Paths.SmsProviderMock.SENT_SMS_DETAILS_API_PATH;
import static com.lykke.tests.api.base.Paths.SmsProviderMock.SMS_BY_MESSAGE_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.SmsProviderMock.SMS_QUERY_PAGINATED_API_PATH;

import com.lykke.tests.api.service.smsprovidermock.model.PaginatedSmsRequestModel;
import com.lykke.tests.api.service.smsprovidermock.model.SendSmsRequestModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SmsProviderMockUtils {

    Response sendSms(SendSmsRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(SEND_SMS_API_PATH)
                .thenReturn();
    }

    public static Response querySms(PaginatedSmsRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(SMS_QUERY_PAGINATED_API_PATH)
                .thenReturn();
    }

    Response deleteSms(String messageId) {
        return getHeader()
                .delete(SMS_BY_MESSAGE_ID_API_PATH.apply(messageId))
                .thenReturn();
    }

    public static Response getSentSms() {
        return getHeader()
                .get(SENT_SMS_API_PATH)
                .thenReturn();
    }

    Response getSentSmsDetails(String messageId) {
        return getHeader()
                ////33 .queryParam("messageId", messageId)
                ////33  .queryParam("id", messageId) // 500
                ////33   .body(messageId) // 500
                ////33    .body("\"" + messageId + "\"") // 500
                .get(SENT_SMS_DETAILS_API_PATH)
                .thenReturn();
    }
}
