package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.Customer.CUSTOMER_API_PARTNER_MESSAGES_BY_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.CUSTOMER_API_PARTNER_MESSAGES_API_PATH;

import com.lykke.api.testing.api.common.QueryParamsUtils;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PartnersMessagesUtils {

    @Deprecated
    public Response getPartnerMessageById_Deprecated(String partnerMessageId, String token) {
        return getHeader(token)
                .get(CUSTOMER_API_PARTNER_MESSAGES_BY_ID_API_PATH.apply(partnerMessageId))
                .thenReturn();
    }

    public Response getPartnerMessageById(String partnerMessageId, String token) {
        return getHeader(token)
                .queryParams(QueryParamsUtils.getQueryParams(ByPartnerMessageId
                        .builder()
                        .partnerMessageId(partnerMessageId)
                        .build()))
                .get(CUSTOMER_API_PARTNER_MESSAGES_API_PATH)
                .thenReturn();
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static final class ByPartnerMessageId {

        private String partnerMessageId;
    }
}
