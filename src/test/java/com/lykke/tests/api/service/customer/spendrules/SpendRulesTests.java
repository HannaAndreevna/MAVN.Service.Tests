package com.lykke.tests.api.service.customer.spendrules;

import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.Currency.AED_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.CustomerCredentials.getUsetToken;
import static com.lykke.tests.api.common.CommonConsts.Image.IMAGE_URL;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.prerequisites.BurnRules.createBurnRuleWithAllContentTypes;
import static com.lykke.tests.api.common.prerequisites.BurnRules.createBurnRuleWithENContents;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.deleteBurnRuleById;
import static com.lykke.tests.api.service.campaigns.BurnRulesUtils.getPaginatedBurnRulesList;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getCustomerToken;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.SpendRulesUtils.getSpendRuleById;
import static com.lykke.tests.api.service.customer.SpendRulesUtils.getSpendRuleById_Deprecated;
import static com.lykke.tests.api.service.customer.SpendRulesUtils.getSpendRuleById_Response;
import static com.lykke.tests.api.service.customer.SpendRulesUtils.getSpendRuleById_Response_Deprecated;
import static com.lykke.tests.api.service.customer.SpendRulesUtils.getSpendRules;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.DefectIds;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.customer.SpendRulesUtils.ValidationErrorResponse;
import com.lykke.tests.api.service.customer.model.spendrule.ConversionRateErrorCode;
import com.lykke.tests.api.service.customer.model.spendrule.SpendRuleListDetailsModel;
import java.util.Arrays;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class SpendRulesTests extends BaseApiTest {

    private static final Double AMOUNT_IN_TOKENS = 12.0;
    private static final int AMOUNT_IN_CURRENCY = 21;
    private static final String BURN_RULE_CONTENT_TITLE_EN_VALUE = "ENGLISH TITLE";
    private static final String BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE = "ENGLISH DESCRIPTION";
    private static final String imageURL = IMAGE_URL;
    private static final String ERROR_FIELD = "error";
    private static final String MESSAGE_FIELD = "message";
    private static final String ERROR_TYPE = "ModelValidationFailed";
    private static final String ERROR_MESSAGE = "The value '%s' is not valid.";
    private static final String CURRENCY = AED_CURRENCY;
    private static final String SPEND_RULE_NOT_FOUND_ERROR_MESSAGE = "Spend Rule not found";

    private CustomerInfo customerData;
    private String customerToken;

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

        customerData = registerDefaultVerifiedCustomer();
        customerToken = getUserToken(customerData);
    }

    // TODO: we don't needd to delete all the burn rules to get any
    @Test
    @UserStoryId(storyId = {1736, 2174, 3870})
    @Tag(SMOKE_TEST)
    void shouldGetSpendRules() {
        val spendRuleId = createBurnRuleWithAllContentTypes(true);

        val actualResultCollection = getSpendRules(customerToken);
        val actualResult = Arrays.stream(actualResultCollection)
                .filter(rule -> rule.getId().equalsIgnoreCase(spendRuleId))
                .findFirst()
                .orElse(new SpendRuleListDetailsModel());

        assertAll(
                () -> assertEquals(spendRuleId, actualResult.getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(CURRENCY, actualResult.getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertTrue(actualResult.getImageUrl().contains(imageURL)),
                // FAL-3870
                () -> assertTrue(0 < actualResult.getOrder())
        );
    }

    @Test
    @UserStoryId(storyId = {1736, 2174, 3870})
    @Tag(SMOKE_TEST)
    void shouldGetSpendRulesWhenNoImageIsAdded() {
        val spendRuleId = createBurnRuleWithAllContentTypes(false);

        val actualResult = getSpendRules(customerToken);

        assertAll(
                () -> assertEquals(spendRuleId, actualResult[0].getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_EN_VALUE, actualResult[0].getTitle()),
                () -> assertEquals(CURRENCY, actualResult[0].getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult[0].getDescription()),
                () -> assertEquals("", actualResult[0].getImageUrl()),
                // FAL-3870
                () -> assertEquals(235234, actualResult[0].getOrder())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2245, 3870})
    void shouldGetSpendRuleById_Deprecated() {
        val spendRuleId = createBurnRuleWithAllContentTypes(true);
        createBurnRuleWithENContents(true);

        val actualResult = getSpendRuleById_Deprecated(spendRuleId, customerToken);

        assertAll(
                () -> assertEquals(spendRuleId, actualResult.getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(AMOUNT_IN_TOKENS, Double.valueOf(actualResult.getAmountInTokens())),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getAmountInCurrency()),
                () -> assertEquals(CURRENCY, actualResult.getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertTrue(actualResult.getImageUrl().contains(imageURL)),
                // FAL-3870
                () -> assertTrue(0 < actualResult.getOrder())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {3815, 3870})
    void shouldGetSpendRuleById() {
        val spendRuleId = createBurnRuleWithAllContentTypes(true);
        createBurnRuleWithENContents(true);

        val actualResult = getSpendRuleById(spendRuleId, customerToken);

        assertAll(
                () -> assertEquals(spendRuleId, actualResult.getId()),
                () -> assertEquals(BURN_RULE_CONTENT_TITLE_EN_VALUE, actualResult.getTitle()),
                () -> assertEquals(AMOUNT_IN_TOKENS, Double.valueOf(actualResult.getAmountInTokens())),
                () -> assertEquals(AMOUNT_IN_CURRENCY, actualResult.getAmountInCurrency()),
                () -> assertEquals(CURRENCY, actualResult.getCurrencyName()),
                () -> assertEquals(BURN_RULE_CONTENT_DESCRIPTION_EN_VALUE, actualResult.getDescription()),
                () -> assertTrue(actualResult.getImageUrl().contains(imageURL)),
                // FAL-3870
                () -> assertTrue(0 < actualResult.getOrder())
        );
    }

    @Test
    @UserStoryId(storyId = 2245)
    void shouldNotGetSpendRuleWhenIdIsNotValid_Deprecated() {
        val invalidSpendRule = "dedaeqeq-dadaddede-deead333-afe";
        getSpendRuleById_Response_Deprecated(invalidSpendRule, customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(ERROR_TYPE))
                .body(MESSAGE_FIELD, equalTo(String.format(ERROR_MESSAGE, invalidSpendRule)));
    }

    @Test
    @UserStoryId(storyId = {2245, 3890})
    @DefectIds(3890)
    void shouldNotReturnContentWhenSpendRuleDoesNotExist_Deprecated() {
        val expectedResult = ValidationErrorResponse
                .builder()
                .error(ConversionRateErrorCode.SPEND_RULE_NOT_FOUND.getCode())
                .message(SPEND_RULE_NOT_FOUND_ERROR_MESSAGE)
                .build();

        val actualResult = getSpendRuleById_Response_Deprecated(getRandomUuid(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(3815)
    void shouldNotGetSpendRuleWhenIdIsNotValid() {
        val invalidSpendRule = "dedaeqeq-dadaddede-deead333-afe";
        getSpendRuleById_Response(invalidSpendRule, customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(ERROR_TYPE))
                .body(MESSAGE_FIELD, equalTo(String.format(ERROR_MESSAGE, invalidSpendRule)));
    }

    @Test
    @UserStoryId(storyId = {3815, 3890})
    @DefectIds(3890)
    void shouldNotReturnContentWhenSpendRuleDoesNotExist() {
        val expectedResult = ValidationErrorResponse
                .builder()
                .error(ConversionRateErrorCode.SPEND_RULE_NOT_FOUND.getCode())
                .message(SPEND_RULE_NOT_FOUND_ERROR_MESSAGE)
                .build();

        val actualResult = getSpendRuleById_Response(getRandomUuid(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }
}
