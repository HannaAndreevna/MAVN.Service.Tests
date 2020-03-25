package com.lykke.tests.api.service.notificationsystemadapter;

import static com.lykke.api.testing.api.base.RequestHeader.getHeaderWithKey;
import static com.lykke.tests.api.base.PathConsts.NotificationSystemAdapterService.KEYS_PATH;
import static com.lykke.tests.api.common.CommonConsts.NOTIFICATION_ADAPTER_API_KEY;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PersonalDataAdapterUtils {

    private static final String CUSTOMER_ID_FIELD = "customerId";

    public Response getKeysByCustomerIdAndNamespace(String customerId, String namespace, String url) {
        return getHeaderWithKey(NOTIFICATION_ADAPTER_API_KEY)
                .param(CUSTOMER_ID_FIELD, customerId)
                .get(url + KEYS_PATH.getFilledInPath(namespace));
    }
}
