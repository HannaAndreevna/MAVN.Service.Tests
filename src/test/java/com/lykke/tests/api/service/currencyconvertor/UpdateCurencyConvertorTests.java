package com.lykke.tests.api.service.currencyconvertor;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.createCurrencies;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.getCurrenciesByCurrencyCode;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.updateCurrencies;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
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

public class UpdateCurencyConvertorTests extends BaseApiTest {

    private static final String CURRENCY_ASSET_CODE_01 = generateRandomString();
    private static final String CURRENCY_ASSET_LABEL = generateRandomString();
    private static final String CURRENCY_ASSET_CODE_02 = generateRandomString();
    private static final Float CURRENCY_RATE = 2.6f;
    private static final String ERROR_CODE_FIELD = "ErrorCode";
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final String CURRENCY_ASSET_CODE_0_FIELD = "BaseAsset[0]";
    private static final String CURRENCY_ASSET_LABEL_0_FIELD = "QuoteAsset[0]";
    private static final String CURRENCY_RATE_00_FIELD = "Rate[0]";
    private static final String BASE_ASSET_REQUIRED_MESSAGE = "Base asset required";
    private static final String QUOTE_ASSET_REQUIRED_MESSAGE = "Quote asset required";
    private static final String THE_RATE_SHOULD_BE_GREATER_THAN_ZERO_MESSAGE = "The rate should be greater than zero";
    private static CurrencyRateRequest currencyConvertor;
    private static CurrencyRateRequestBuilder baseCurrencyConvertor;

    @BeforeAll
    static void setup() {
        baseCurrencyConvertor = CurrencyRateRequest
                .builder()
                .baseAsset(CURRENCY_ASSET_CODE_01)
                .quoteAsset(CURRENCY_ASSET_LABEL)
                .rate(CURRENCY_RATE);

        currencyConvertor = baseCurrencyConvertor
                .build();

        createCurrencies(currencyConvertor)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    private static Stream<Arguments> invalidParameters() {
        return Stream.of(
                of(
                        baseCurrencyConvertor.baseAsset(EMPTY).build(),
                        CURRENCY_ASSET_CODE_0_FIELD,
                        BASE_ASSET_REQUIRED_MESSAGE),
                of(
                        baseCurrencyConvertor.baseAsset(null).build(),
                        CURRENCY_ASSET_CODE_0_FIELD,
                        BASE_ASSET_REQUIRED_MESSAGE),
                of(
                        baseCurrencyConvertor.baseAsset(CURRENCY_ASSET_CODE_01).quoteAsset(EMPTY).build(),
                        CURRENCY_ASSET_LABEL_0_FIELD,
                        QUOTE_ASSET_REQUIRED_MESSAGE),
                of(
                        baseCurrencyConvertor.quoteAsset(null).build(),
                        CURRENCY_ASSET_LABEL_0_FIELD,
                        QUOTE_ASSET_REQUIRED_MESSAGE),
                of(
                        baseCurrencyConvertor.rate(0f).build(),
                        CURRENCY_RATE_00_FIELD,
                        THE_RATE_SHOULD_BE_GREATER_THAN_ZERO_MESSAGE),
                of(
                        baseCurrencyConvertor.rate(-1f).build(),
                        CURRENCY_RATE_00_FIELD,
                        THE_RATE_SHOULD_BE_GREATER_THAN_ZERO_MESSAGE)
        );
    }

    @Test
    @UserStoryId(storyId = 666)
    void shouldUpdateCurrency() {

        val expectedOriginalCurrencyRate = ConverterResponse
                .builder()
                .amount(0)
                .errorCode(ConverterErrorCode.NONE)
                .build();
        val actualOriginalCurrencyRate = getCurrenciesByCurrencyCode(CURRENCY_ASSET_CODE_01, CURRENCY_ASSET_LABEL)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConverterResponse.class);
        assertEquals(expectedOriginalCurrencyRate, actualOriginalCurrencyRate);

        val currencyRate = 2.3f;

        val newCurrencyConvertor = CurrencyRateRequest
                .builder()
                .baseAsset(CURRENCY_ASSET_CODE_01)
                .quoteAsset(CURRENCY_ASSET_LABEL)
                .rate(currencyRate)
                .build();

        updateCurrencies(newCurrencyConvertor)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo(ConverterErrorCode.NONE.getCode()))
                .body(ERROR_MESSAGE_FIELD, nullValue());

        val actualResult = getCurrenciesByCurrencyCode(CURRENCY_ASSET_CODE_01, CURRENCY_ASSET_LABEL)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConverterResponse.class);

        assertEquals(expectedOriginalCurrencyRate, actualResult);
    }

    @ParameterizedTest
    @MethodSource("invalidParameters")
    @UserStoryId(storyId = 666)
    void shouldNotUpdateCurrencyWhenOneOfTheParamsIsNotValid(CurrencyRateRequest requestModel, String field,
            String message) {
        updateCurrencies(requestModel)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(field, equalTo(message));
    }
}
