package com.lykke.tests.api.service.currencyconvertor;

import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.createCurrencies;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.getCurrenciesByCurrencyCode;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.GenerateUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.currencyconvertor.model.ConverterErrorCode;
import com.lykke.tests.api.service.currencyconvertor.model.ConverterResponse;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateRequest;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateRequest.CurrencyRateRequestBuilder;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CreateCurrencyConvertorTests extends BaseApiTest {

    private static final String CURRENCY_ASSET_CODE = GenerateUtils.generateRandomString();
    private static final String CURRENCY_ASSET_LABEL = GenerateUtils.generateRandomString();
    private static final Float CURRENCY_RATE = 2.6f;
    private static final String ERROR_CODE_FIELD = "ErrorCode";
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final String CURRENCY_ASSET_CODE_0_FIELD = "BaseAsset[0]";
    private static final String CURRENCY_ASSET_LABEL_0_FIELD = "QuoteAsset[0]";
    private static final String CURRENCY_RATE_00_FIELD = "Rate[0]";
    private static final String BASE_ASSET_REQUIRED_MESSAGE = "Base asset required";
    private static final String QUOTE_ASSET_REQUIRED_MESSAGE = "Quote asset required";
    private static final String THE_RATE_SHOULD_BE_GREATER_THAN_ZERO_MESSAGE = "The rate should be greater than zero";
    private static CurrencyRateRequest currencyRateRequest;
    private static CurrencyRateRequestBuilder baseCurrencyRateRequest;

    @BeforeAll
    static void setup() {
        baseCurrencyRateRequest = CurrencyRateRequest
                .builder()
                .baseAsset(CURRENCY_ASSET_CODE)
                .quoteAsset(CURRENCY_ASSET_LABEL)
                .rate(CURRENCY_RATE);

        currencyRateRequest = baseCurrencyRateRequest
                .build();
    }

    private static Stream<Arguments> invalidParameters() {
        return Stream.of(
                of(
                        baseCurrencyRateRequest.baseAsset(EMPTY).build(),
                        CURRENCY_ASSET_CODE_0_FIELD,
                        BASE_ASSET_REQUIRED_MESSAGE),
                of(
                        baseCurrencyRateRequest.baseAsset(null).build(),
                        CURRENCY_ASSET_CODE_0_FIELD,
                        BASE_ASSET_REQUIRED_MESSAGE),
                of(
                        baseCurrencyRateRequest.baseAsset(CURRENCY_ASSET_CODE).quoteAsset(EMPTY).build(),
                        CURRENCY_ASSET_LABEL_0_FIELD,
                        QUOTE_ASSET_REQUIRED_MESSAGE),
                of(
                        baseCurrencyRateRequest.quoteAsset(null).build(),
                        CURRENCY_ASSET_LABEL_0_FIELD,
                        QUOTE_ASSET_REQUIRED_MESSAGE),
                of(
                        baseCurrencyRateRequest.rate(0f).build(),
                        CURRENCY_RATE_00_FIELD,
                        THE_RATE_SHOULD_BE_GREATER_THAN_ZERO_MESSAGE),
                of(
                        baseCurrencyRateRequest.rate(-1f).build(),
                        CURRENCY_RATE_00_FIELD,
                        THE_RATE_SHOULD_BE_GREATER_THAN_ZERO_MESSAGE)
        );
    }

    @Test
    @UserStoryId(storyId = 666)
    void shouldCreateCurrency() {
        val expectedResult = ConverterResponse
                .builder()
                .amount(0)
                .errorCode(ConverterErrorCode.NONE)
                .build();

        createCurrencies(currencyRateRequest)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo("None"))
                .body(ERROR_MESSAGE_FIELD, nullValue());

        val actualResult = getCurrenciesByCurrencyCode(CURRENCY_ASSET_CODE, CURRENCY_ASSET_LABEL)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConverterResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("invalidParameters")
    @UserStoryId(storyId = 666)
    void shouldCheckForInvalidValues(CurrencyRateRequest requestModel, String field, String message) {
        createCurrencies(requestModel)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(field, equalTo(message));
    }
}
