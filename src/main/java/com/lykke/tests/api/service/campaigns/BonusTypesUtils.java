package com.lykke.tests.api.service.campaigns;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.ACTIVE_TYPES_PATH;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.BY_TYPE;
import static com.lykke.tests.api.base.Paths.AdminApi.ADMIN_API_CONDITIONS_PATH;
import static com.lykke.tests.api.base.Paths.BONUS_TYPES_API_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.campaigns.model.BonusType;
import com.lykke.tests.api.service.campaigns.model.BonusTypeListResponseModel;
import com.lykke.tests.api.service.campaigns.model.BonusTypeResponseModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Arrays;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class BonusTypesUtils {

    @Step("Get conditions")
    Response getConditions() {
        return getHeader()
                .get(BONUS_TYPES_API_PATH);
    }

    public BonusTypeListResponseModel getBonusTypes() {
        return getHeader()
                .get(BONUS_TYPES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusTypeListResponseModel.class);
    }

    BonusTypeListResponseModel getActiveConditions() {
        return getHeader()
                .get(BONUS_TYPES_API_PATH + ACTIVE_TYPES_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusTypeListResponseModel.class);
    }

    @Step("Get bonus type {type}")
    Response getBonusTypesByType(String type) {
        return getHeader()
                .get(BONUS_TYPES_API_PATH + BY_TYPE.getFilledInPath(type));
    }

    public Response getConditionsAsAdmin(String token) {
        return getHeader(token)
                .get(ADMIN_API_CONDITIONS_PATH);
    }

    BonusType createConditionObject(String type, String displayName, Boolean isAvailable) {
        return BonusType
                .builder()
                .type(type)
                .displayName(displayName)
                .isAvailable(isAvailable)
                .build();
    }

    BonusTypeResponseModel getRandomBonusType() {
        val bonusTypes = getConditions()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusTypeListResponseModel.class)
                .getBonusTypes();
        int skipNumberOfBonusTypes = (int) (Math.random() * (bonusTypes.length - 2));
        return Arrays.stream(bonusTypes).skip(skipNumberOfBonusTypes).findFirst()
                .orElse(new BonusTypeResponseModel());
    }
}
