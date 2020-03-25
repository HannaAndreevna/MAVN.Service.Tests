package com.lykke.tests.api.service.campaigns;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.JsonConversionUtils.convertFromJsonFile;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.HelperUtils.getTestFilePath;
import static com.lykke.tests.api.service.campaigns.BonusTypesUtils.getActiveConditions;
import static com.lykke.tests.api.service.campaigns.BonusTypesUtils.getBonusTypes;
import static com.lykke.tests.api.service.campaigns.BonusTypesUtils.getBonusTypesByType;
import static com.lykke.tests.api.service.campaigns.BonusTypesUtils.getRandomBonusType;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.ResourcesConsts.Campaigns;
import com.lykke.tests.api.service.campaigns.model.BonusTypeListResponseModel;
import com.lykke.tests.api.service.campaigns.model.BonusTypeResponseModel;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CampaignBonusTypesTests extends BaseApiTest {

    private static String conditionType;
    private static String conditionDisplayName;
    private static String displayName = generateRandomString();

    @BeforeEach
    void setup() {
        conditionType = generateRandomString();
        conditionDisplayName = generateRandomString();
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {572, 2286})
    @SneakyThrows
    void shouldGetConditionTypes() {
        val jsonFile = getTestFilePath(Campaigns.BONUS_TYPES);

        val expectedResult = convertFromJsonFile(jsonFile, BonusTypeListResponseModel.class);
        val actualResult = getBonusTypes();

        ////xx
        assertArrayEquals(expectedResult.getBonusTypes(), actualResult.getBonusTypes());
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {572, 2286})
    @SneakyThrows
    void shouldGetActiveConditionTypes() {
        val jsonFile = getTestFilePath(Campaigns.ACTIVE_CONDIITONS);

        val expectedResult = convertFromJsonFile(jsonFile, BonusTypeListResponseModel.class);

        val actualResult = getActiveConditions();
////xx
        assertArrayEquals(expectedResult.getBonusTypes(), actualResult.getBonusTypes());
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 1201)
    void shouldGetBonusTypesByType() {
        val randomBonusType = getRandomBonusType();
        val result = getBonusTypesByType(randomBonusType.getType())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusTypeResponseModel.class);

        Assertions.assertAll(
                () -> assertEquals(randomBonusType.getType(), result.getType()),
                () -> assertEquals(randomBonusType.getDisplayName(), result.getDisplayName()),
                () -> assertEquals("None", result.getErrorCode().getCode()),
                () -> assertEquals(null, result.getErrorMessage())
        );
    }

    @Test
    @UserStoryId(storyId = 1201)
    void shouldReturnNoContentWhenThereIsNoSuchBonusType() {
        val type = generateRandomString().toLowerCase();

        getBonusTypesByType(type)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }
}
