package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.AdminApi.BONUS_TYPES_API_PATH;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BonusTypesUtils {

    public Response getAllBonusTypes(String token) {
        return getHeader(token)
                .get(BONUS_TYPES_API_PATH)
                .thenReturn();
    }
}
