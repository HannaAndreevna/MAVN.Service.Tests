package com.lykke.tests.api.service.campaigns.mobile;

import static com.lykke.tests.api.common.CommonConsts.Currency.AED_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Image.IMAGE_URL;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.prerequisites.BurnRules.createBurnRuleWithAllContentTypes;
import static com.lykke.tests.api.common.prerequisites.BurnRules.createBurnRuleWithENContents;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.deleteBurnRuleById;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getPaginatedBurnRulesList;
import static com.lykke.tests.api.service.campaigns.MobileUtils.getMobileBurnRuleById;
import static com.lykke.tests.api.service.campaigns.MobileUtils.getMobileBurnRuleByIdValidationResponse;
import static com.lykke.tests.api.service.campaigns.MobileUtils.getMobileBurnRuleWithId;
import static com.lykke.tests.api.service.campaigns.MobileUtils.getMobileBurnRules;
import static com.lykke.tests.api.service.campaigns.MobileUtils.getMobileBurnRulesByIdResp;
import static com.lykke.tests.api.service.campaigns.MobileUtils.getMobileBurnRulesResp;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.service.campaigns.model.mobile.BurnRuleLocalizedResponse;
import com.lykke.tests.api.service.campaigns.model.mobile.MobileGetByIdRequest;
import java.util.Arrays;
import lombok.val;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class MobileBurnRulesTests extends BaseApiTest {

    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final String MODEL_ERRORS_LANGUAGE_FIELD = "ModelErrors.language[0]";
    private static final String INVALID_VALUE_MSG = "The value 'Bg' is not valid.";
    private static final Localization LOCALIZATION_EN = Localization.EN;
    private static final Localization LOCALIZATION_AR = Localization.AR;
    private static final Double AMOUNT_IN_TOKENS = 12.0;
    private static final int AMOUNT_IN_CURRENCY = 21;
    private static final String BURN_RULE_CONTENT_TITLE_EN_VALUE = "ENGLISH TITLE";
    private static final String BURN_RULE_CONTENT_TITLE_AR_VALUE = "ARABIC TITLE";
    private static final String BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE = "ENGLISH DESCRIPTION";
    private static final String BURN_RULE_CONTENT_DESCRIPTION_AR_VALUE = "ARABIC DESCRIPTION";
    private static final String imageURL = IMAGE_URL;
    private static final String CURRENCY = AED_CURRENCY;

    private static void deleteAllBurnRules() {
        /*
        while (!(getPaginatedBurnRulesList("", 1, 500).getTotalCount() == 0)) {
            String burnRuleId = getPaginatedBurnRulesList("", 1, 1).getBurnRules()[0].getId();

            deleteBurnRuleById(burnRuleId)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK);
        }
        */
    }

    @BeforeEach
    void setup() {
        deleteAllBurnRules();
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1737, 2245, 3871})
    void shouldGetEnMobileBurnRules() {
        val burnRuleId = createBurnRuleWithAllContentTypes(true);
        val actualResultCollection = getMobileBurnRules(LOCALIZATION_EN);
        val actualResult = Arrays.stream(actualResultCollection)
                .filter(rule -> rule.getId().equalsIgnoreCase(burnRuleId))
                .findFirst()
                .orElse(new BurnRuleLocalizedResponse());

        assertAll(
                () -> assertEquals(burnRuleId, actualResult.getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(AMOUNT_IN_TOKENS.toString(), actualResult.getAmountInTokens()),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getAmountInCurrency()),
                () -> assertEquals(CURRENCY, actualResult.getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertTrue(actualResult.getImageUrl().contains(imageURL)),
                // FAL-3871
                () -> assertTrue(0 < actualResult.getOrder())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1737, 2245, 3915, 3871})
    void shouldGetArMobileBurnRules() {
        val burnRuleId = createBurnRuleWithAllContentTypes(true);
        val actualResultCollection = getMobileBurnRules(LOCALIZATION_AR);
        val actualResult = Arrays.stream(actualResultCollection)
                .filter(rule -> rule.getId().equalsIgnoreCase(burnRuleId) && rule.getOrder() > 0)
                .findFirst()
                .orElse(new BurnRuleLocalizedResponse());

        assertAll(
                () -> assertEquals(burnRuleId, actualResult.getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_AR_VALUE, actualResult.getTitle()),
                () -> assertEquals(AMOUNT_IN_TOKENS.toString(), actualResult.getAmountInTokens()),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getAmountInCurrency()),
                () -> assertEquals(CURRENCY, actualResult.getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_AR_VALUE, actualResult.getDescription()),
                () -> assertTrue(actualResult.getImageUrl().contains(imageURL)),
                // FAL-3915
                () -> assertNotNull(actualResult.getPrice()),
                // FAL-3871
                () -> assertTrue(0 < actualResult.getOrder())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1737, 2245})
    void shouldGetEnMobileBurnRulesWhenNotSpecified() {
        val burnRuleId = createBurnRuleWithAllContentTypes(true);
        val actualResult = getMobileBurnRuleWithId(burnRuleId);

        assertAll(
                () -> assertEquals(burnRuleId, actualResult.getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(AMOUNT_IN_TOKENS.toString(), actualResult.getAmountInTokens()),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getAmountInCurrency()),
                () -> assertEquals(CURRENCY, actualResult.getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertTrue(actualResult.getImageUrl().contains(imageURL))
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1737, 2245})
    void shouldGetEnMobileBurnRulesWhenThereIsNoArContent() {
        val burnRuleId = createBurnRuleWithENContents(true);
        val actualResult = getMobileBurnRuleWithId(LOCALIZATION_AR, burnRuleId);

        assertAll(
                () -> assertEquals(burnRuleId, actualResult.getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(AMOUNT_IN_TOKENS.toString(), actualResult.getAmountInTokens()),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getAmountInCurrency()),
                () -> assertEquals(CURRENCY, actualResult.getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertTrue(actualResult.getImageUrl().contains(imageURL))
        );
    }

    @Test
    @UserStoryId(storyId = 1737)
    void shouldNotGetMobileBurnRulesWhenLanguageIsNotValid() {
        getMobileBurnRulesResp("Bg")
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, CoreMatchers.equalTo(INVALID_VALUE_MSG))
                .body(MODEL_ERRORS_LANGUAGE_FIELD, CoreMatchers.equalTo(INVALID_VALUE_MSG));
    }


    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2245, 3871})
    void shouldGetMobileBurnRuleByIdWhenLanguageIsNotSpecified_EN() {
        val burnRuleId = createBurnRuleWithAllContentTypes(true);
        createBurnRuleWithENContents(true);

        val actualResult = getMobileBurnRuleById(burnRuleId);

        assertAll(
                () -> assertEquals(burnRuleId, actualResult.getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(AMOUNT_IN_TOKENS.toString(), actualResult.getAmountInTokens()),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getAmountInCurrency()),
                () -> assertEquals(CURRENCY, actualResult.getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertTrue(actualResult.getImageUrl().contains(imageURL)),
                // FAL-3871
                () -> assertTrue(0 < actualResult.getOrder())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2245)
    void shouldGetMobileBurnRuleByIdAndLanguage_EN() {
        val burnRuleId = createBurnRuleWithAllContentTypes(true);
        createBurnRuleWithENContents(true);

        val actualResult = getMobileBurnRuleById(burnRuleId, LOCALIZATION_EN);

        assertAll(
                () -> assertEquals(burnRuleId, actualResult.getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(AMOUNT_IN_TOKENS.toString(), actualResult.getAmountInTokens()),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getAmountInCurrency()),
                () -> assertEquals(CURRENCY, actualResult.getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertTrue(actualResult.getImageUrl().contains(imageURL))
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2245)
    void shouldGetMobileBurnRuleByIdAndLanguage_AR() {
        val burnRuleId = createBurnRuleWithAllContentTypes(true);
        createBurnRuleWithENContents(true);

        val actualResult = getMobileBurnRuleById(burnRuleId, LOCALIZATION_AR);

        assertAll(
                () -> assertEquals(burnRuleId, actualResult.getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_AR_VALUE, actualResult.getTitle()),
                () -> assertEquals(AMOUNT_IN_TOKENS.toString(), actualResult.getAmountInTokens()),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getAmountInCurrency()),
                () -> assertEquals(CURRENCY, actualResult.getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_AR_VALUE, actualResult.getDescription()),
                () -> assertTrue(actualResult.getImageUrl().contains(imageURL))
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 2245)
    void shouldGetEnMobileBurnRuleByIdWhenLanguageIsArButThereIsNoSuchContent() {
        val burnRuleId = createBurnRuleWithENContents(true);
        createBurnRuleWithENContents(true);

        val actualResult = getMobileBurnRuleById(burnRuleId, LOCALIZATION_AR);

        assertAll(
                () -> assertEquals(burnRuleId, actualResult.getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(AMOUNT_IN_TOKENS.toString(), actualResult.getAmountInTokens()),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getAmountInCurrency()),
                () -> assertEquals(CURRENCY, actualResult.getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertTrue(actualResult.getImageUrl().contains(imageURL))
        );
    }

    @Test
    @UserStoryId(storyId = 2245)
    void shouldNotGetBurnRuleWhenLanguageIsNotValid() {
        val burnRuleId = createBurnRuleWithENContents(true);
        getMobileBurnRulesByIdResp(burnRuleId, "Bg")
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, CoreMatchers.equalTo(INVALID_VALUE_MSG))
                .body(MODEL_ERRORS_LANGUAGE_FIELD, CoreMatchers.equalTo(INVALID_VALUE_MSG));
    }

    @ParameterizedTest
    @CsvSource({"fa32b67a-3945-44e0-4a11-08d71fd82b3",
            "fa32b67a-3945-44e0-4a11-08d71fd82b312",
            "saxas"})
    @UserStoryId(storyId = 2245)
    void shouldValidateBurnRuleIdFormat(String invalidBurnRuleId) {
        val requestObject = MobileGetByIdRequest
                .builder()
                .burnRuleId(invalidBurnRuleId)
                .build();

        val actualResult = getMobileBurnRuleByIdValidationResponse(requestObject);

        assertEquals(requestObject.getValidationResponse(), actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(4295)
    void shouldGetDeletedBurnRuleById() {
        val burnRuleId = createBurnRuleWithAllContentTypes(true);
        deleteBurnRuleById(burnRuleId)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val actualResult = getMobileBurnRuleWithId(burnRuleId, true);

        assertAll(
                () -> assertEquals(burnRuleId, actualResult.getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(AMOUNT_IN_TOKENS.toString(), actualResult.getAmountInTokens()),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getAmountInCurrency()),
                () -> assertEquals(CURRENCY, actualResult.getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertTrue(actualResult.getImageUrl().contains(imageURL))
        );
    }
}
