package com.lykke.tests.api.service.eligibilityengine;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.Currency.AED_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Currency.USD_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.createPartnerCredentials;
import static com.lykke.tests.api.service.credentials.model.CredentialsError.NONE;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.eligibilityengine.EligibilityEngineUtils.getConditionAmount;
import static com.lykke.tests.api.service.eligibilityengine.EligibilityEngineUtils.getConditionRate;
import static com.lykke.tests.api.service.eligibilityengine.EligibilityEngineUtils.getEarnRuleAmount;
import static com.lykke.tests.api.service.eligibilityengine.EligibilityEngineUtils.getEarnRuleRate;
import static com.lykke.tests.api.service.eligibilityengine.EligibilityEngineUtils.getPartnerAmount;
import static com.lykke.tests.api.service.eligibilityengine.EligibilityEngineUtils.getPartnerRate;
import static com.lykke.tests.api.service.eligibilityengine.EligibilityEngineUtils.getSpendRuleAmount;
import static com.lykke.tests.api.service.eligibilityengine.EligibilityEngineUtils.getSpendRuleRate;
import static com.lykke.tests.api.service.eligibilityengine.model.EligibilityEngineError.SPEND_RULE_NOT_FOUND;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.SOME_EXTERNAL_ID;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationId;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.DefectIds;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.eligibilityengine.model.ConversionSource;
import com.lykke.tests.api.service.eligibilityengine.model.ConvertAmountByConditionRequest;
import com.lykke.tests.api.service.eligibilityengine.model.ConvertAmountByEarnRuleRequest;
import com.lykke.tests.api.service.eligibilityengine.model.ConvertAmountByEarnRuleResponse;
import com.lykke.tests.api.service.eligibilityengine.model.ConvertAmountBySpendRuleRequest;
import com.lykke.tests.api.service.eligibilityengine.model.ConvertAmountBySpendRuleResponse;
import com.lykke.tests.api.service.eligibilityengine.model.ConvertOptimalByPartnerRequest;
import com.lykke.tests.api.service.eligibilityengine.model.ConvertOptimalByPartnerResponse;
import com.lykke.tests.api.service.eligibilityengine.model.CurrencyRateByConditionRequest;
import com.lykke.tests.api.service.eligibilityengine.model.CurrencyRateByConditionResponse;
import com.lykke.tests.api.service.eligibilityengine.model.CurrencyRateByEarnRuleRequest;
import com.lykke.tests.api.service.eligibilityengine.model.CurrencyRateByEarnRuleResponse;
import com.lykke.tests.api.service.eligibilityengine.model.CurrencyRateBySpendRuleRequest;
import com.lykke.tests.api.service.eligibilityengine.model.CurrencyRateBySpendRuleResponse;
import com.lykke.tests.api.service.eligibilityengine.model.EligibilityEngineError;
import com.lykke.tests.api.service.eligibilityengine.model.OptimalCurrencyRateByPartnerRequest;
import com.lykke.tests.api.service.eligibilityengine.model.OptimalCurrencyRateByPartnerResponse;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateResponse;
import java.util.function.Function;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class EligibilityEngineTests extends BaseApiTest {

    public static final String ZERO_AMOUNT = "0";
    public static final String ZERO_RATE = "0";
    public static final String EMPTY_GUID = "00000000-0000-0000-0000-000000000000";
    private static final Function<String, String> CLIENT_PREFIX = (id) -> "non-existing-client_" + id;
    private static final Function<String, String> EARN_RULE_NOT_FOUND_MESSAGE = (id) -> String
            .format("Earn rule with id '%s' cannot be found.", id);
    private static final Function<String, String> SPEND_RULE_NOT_FOUND_MESSAGE = (id) -> String
            .format("Spend rule with id '%s' cannot be found.", id);
    private static final Function<String, String> CONDITION_NOT_FOUND_MESSAGE = (id) -> String
            .format("Condition with id '%s' cannot be found.", id);
    private static String partnerId;
    private static String partnerPassword;
    private static String partnerToken;
    private static String customerId;
    private static String email;
    private static String phone;
    private static String locationId;
    private static String externalLocationId;
    private PartnerCreateResponse partnerData;

    @BeforeEach
    void setUp() {
        partnerId = getRandomUuid();
        partnerPassword = generateValidPassword();
        val customerData = registerDefaultVerifiedCustomer();
        email = customerData.getEmail();
        phone = customerData.getPhoneNumber();
        customerId = customerData.getCustomerId();

        val partnerCredentials = createPartnerCredentials(partnerId, partnerPassword, CLIENT_PREFIX.apply(partnerId));
        assertEquals(NONE, partnerCredentials.getError());

        // TODO: these are working 100%
        partnerId = getRandomUuid();
        val locationSuffix = generateRandomString(10);
        externalLocationId = SOME_EXTERNAL_ID + locationSuffix;
        partnerData = createDefaultPartner(partnerId, partnerPassword, generateRandomString(10),
                locationSuffix);
        locationId = getLocationId(partnerData);
        partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2873, 2898})
    void shouldGetPartnerRate() {
        val expectedResult = OptimalCurrencyRateByPartnerResponse
                .optimalCurrencyRateByPartnerResponseBuilder()
                .spendRuleId(null)
                .rate("0.428571428571428571") // ?? some value from API
                .conversionSource(ConversionSource.PARTNER)
                .errorCode(EligibilityEngineError.NONE)
                .build();

        val actualResult = getPartnerRate(OptimalCurrencyRateByPartnerRequest
                .optimalCurrencyRateByPartnerRequestBuilder()
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .fromCurrency(AED_CURRENCY)
                .toCurrency(USD_CURRENCY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(OptimalCurrencyRateByPartnerResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2873, 2898})
    void shouldGetEarnRuleRate() {
        val earnRuleId = getRandomUuid();
        val expectedResult = CurrencyRateByEarnRuleResponse
                .currencyRateByEarnRuleResponseBuilder()
                .earnRuleId(EMPTY_GUID)
                .rate(ZERO_RATE)
                .conversionSource(ConversionSource.BURN_RULE)
                .errorCode(EligibilityEngineError.EARN_RULE_NOT_FOUND)
                .errorMessage(EARN_RULE_NOT_FOUND_MESSAGE.apply(earnRuleId))
                .build();

        val actualResult = getEarnRuleRate(CurrencyRateByEarnRuleRequest
                .currencyRateByEarnRuleRequestBuilder()
                .customerId(customerId)
                .earnRuleId(earnRuleId)
                .fromCurrency(AED_CURRENCY)
                .toCurrency(USD_CURRENCY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateByEarnRuleResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2873, 2898})
    @DefectIds(3217)
    void shouldGetSpendRuleRate() {
        val spendRuleId = getRandomUuid();
        val expectedResult = CurrencyRateBySpendRuleResponse
                .currencyRateBySpendRuleResponseBuilder()
                .spendRuleId(EMPTY_GUID)
                .rate(ZERO_RATE)
                .conversionSource(ConversionSource.BURN_RULE)
                .errorCode(SPEND_RULE_NOT_FOUND)
                .errorMessage(SPEND_RULE_NOT_FOUND_MESSAGE.apply(spendRuleId))
                .build();

        val actualResult = getSpendRuleRate(CurrencyRateBySpendRuleRequest
                .currencyRateBySpendRuleRequestBuilder()
                .customerId(customerId)
                .spendRuleId(spendRuleId)
                .fromCurrency(AED_CURRENCY)
                .toCurrency(USD_CURRENCY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateBySpendRuleResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3502)
    void shouldConditionRate() {
        val conditionId = getRandomUuid();
        val expectedResult = CurrencyRateByConditionResponse
                .currencyRateByConditionResponseBuilder()
                .conditionId("00000000-0000-0000-0000-000000000000")
                .rate(ZERO_RATE)
                .conversionSource(ConversionSource.CONDITION)
                .errorCode(EligibilityEngineError.CONDITION_NOT_FOUND)
                .errorMessage(CONDITION_NOT_FOUND_MESSAGE.apply(conditionId))
                .build();

        val actualResult = getConditionRate(CurrencyRateByConditionRequest
                .currencyRateByConditionRequestBuilder()
                .customerId(customerId)
                .conditionId(conditionId)
                .fromCurrency(AED_CURRENCY)
                .toCurrency(USD_CURRENCY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateByConditionResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2873, 2898})
    void shouldGetPartnerAmount() {
        val expectedResult = ConvertOptimalByPartnerResponse
                .convertOptimalByPartnerResponseBuilder()
                .conversionSource(ConversionSource.PARTNER)
                .build();

        val actualResult = getPartnerAmount(ConvertOptimalByPartnerRequest
                .convertOptimalByPartnerRequestBuilder()
                .customerId(customerId)
                .partnerId(partnerData.getId())
                .fromCurrency(AED_CURRENCY)
                .toCurrency(USD_CURRENCY)
                .amount(ZERO_AMOUNT)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConvertOptimalByPartnerResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2873, 2898})
    void shouldGetEarnRuleAmount() {
        val earnRuleId = getRandomUuid();
        val expectedResult = ConvertAmountByEarnRuleResponse
                .convertAmountByEarnRuleResponseBuilder()
                .earnRuleId(EMPTY_GUID)
                .amount(ZERO_AMOUNT)
                .errorCode(EligibilityEngineError.EARN_RULE_NOT_FOUND)
                .errorMessage(EARN_RULE_NOT_FOUND_MESSAGE.apply(earnRuleId))
                .build();

        val actualResult = getEarnRuleAmount(ConvertAmountByEarnRuleRequest
                .convertAmountByEarnRuleRequestBuilder()
                .customerId(customerId)
                .earnRuleId(earnRuleId)
                .amount(ZERO_AMOUNT)
                .fromCurrency(AED_CURRENCY)
                .toCurrency(USD_CURRENCY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConvertAmountByEarnRuleResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(storyId = {2873, 2898})
    @DefectIds(3217)
    void shouldGetSpendRuleAmount() {
        val spendRuleId = getRandomUuid();
        val expectedResult = ConvertAmountBySpendRuleResponse
                .convertAmountBySpendRuleResponseBuilder()
                .spendRuleId(EMPTY_GUID)
                .amount(ZERO_AMOUNT)
                .usedRate(ZERO_RATE)
                .conversionSource(ConversionSource.BURN_RULE)
                .errorCode(SPEND_RULE_NOT_FOUND)
                .errorMessage(SPEND_RULE_NOT_FOUND_MESSAGE.apply(spendRuleId))
                .build();

        val actualResult = getSpendRuleAmount(ConvertAmountBySpendRuleRequest
                .convertAmountBySpendRuleRequestBuilder()
                .customerId(customerId)
                .spendRuleId(spendRuleId)
                .amount(ZERO_AMOUNT)
                .fromCurrency(AED_CURRENCY)
                .toCurrency(USD_CURRENCY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConvertAmountBySpendRuleResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(3502)
    void shouldGetConditionAmount() {
        val conditionId = getRandomUuid();
        val expectedResult = ConvertAmountBySpendRuleResponse
                .convertAmountBySpendRuleResponseBuilder()
                .amount(ZERO_AMOUNT)
                .errorCode(EligibilityEngineError.CONDITION_NOT_FOUND)
                .errorMessage(CONDITION_NOT_FOUND_MESSAGE.apply(conditionId))
                .build();

        val actualResult = getConditionAmount(ConvertAmountByConditionRequest
                .convertAmountByConditionRequestBuilder()
                .customerId(customerId)
                .conditionId(conditionId)
                .amount(ZERO_AMOUNT)
                .fromCurrency(AED_CURRENCY)
                .toCurrency(USD_CURRENCY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConvertAmountBySpendRuleResponse.class);

        assertEquals(expectedResult, actualResult);
    }
}
