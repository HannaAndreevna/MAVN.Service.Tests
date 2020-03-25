package com.lykke.tests.api.service.currencyconvertor;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.deleteCurrencyRates;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.getCurrencyRates;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.postCurrencyRates;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.putCurrencyRates;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.currencyconvertor.model.ConverterRequest;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateModel;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateRequest;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateResponse;
import com.lykke.tests.api.service.currencyconvertor.model.RateErrorCode;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CurrencyRatesTests extends BaseApiTest {

    private static final String THE_RATE_SHOULD_BE_GREATER_THAN_ZERO_ERROR_MESSAGE = "The rate should be greater than zero";
    private static final String BASE_ASSET_REQUIRED_ERROR_MESSAGE = "Base asset required";
    private static final String QUOTE_ASSET_REQUIRED_ERROR_MESSAGE = "Quote asset required";

    static Stream<Arguments> invalidInputData() {
        return Stream.of(
                of(0, generateRandomString(10), generateRandomString(10),
                        ValidationErrorResponse.builder().rate(new String[]{
                                THE_RATE_SHOULD_BE_GREATER_THAN_ZERO_ERROR_MESSAGE}).build()),
                of(-1, generateRandomString(10), generateRandomString(10),
                        ValidationErrorResponse.builder().rate(new String[]{
                                THE_RATE_SHOULD_BE_GREATER_THAN_ZERO_ERROR_MESSAGE}).build()),
                of(100, EMPTY, generateRandomString(10),
                        ValidationErrorResponse.builder().baseAsset(new String[]{
                                BASE_ASSET_REQUIRED_ERROR_MESSAGE}).build()),
                of(100, generateRandomString(10), EMPTY,
                        ValidationErrorResponse.builder().quoteAsset(new String[]{
                                QUOTE_ASSET_REQUIRED_ERROR_MESSAGE}).build()),
                of(100, EMPTY, EMPTY,
                        ValidationErrorResponse.builder().baseAsset(new String[]{
                                BASE_ASSET_REQUIRED_ERROR_MESSAGE}).quoteAsset(new String[]{
                                QUOTE_ASSET_REQUIRED_ERROR_MESSAGE}).build()),
                of(-1, EMPTY, EMPTY,
                        ValidationErrorResponse.builder().rate(new String[]{
                                THE_RATE_SHOULD_BE_GREATER_THAN_ZERO_ERROR_MESSAGE}).baseAsset(new String[]{
                                BASE_ASSET_REQUIRED_ERROR_MESSAGE}).quoteAsset(new String[]{
                                QUOTE_ASSET_REQUIRED_ERROR_MESSAGE}).build())
        );
    }

    @Test
    @UserStoryId(2644)
    void shouldCreateCurrencyRate() {
        val baseAsset = generateRandomString(10);
        val quoteAsset = generateRandomString(10);
        val rate = (float) Math.random() * 100;
        val expectedResult = CurrencyRateResponse
                .builder()
                .baseAsset(baseAsset)
                .quoteAsset(quoteAsset)
                .rate(rate)
                .errorCode(RateErrorCode.NONE)
                .build();

        val actualResult = postCurrencyRates(CurrencyRateRequest
                .builder()
                .baseAsset(baseAsset)
                .quoteAsset(quoteAsset)
                .rate(rate)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2644)
    void shouldNotCreateAlreadyExistingCurrencyRate() {
        val baseAsset = generateRandomString(10);
        val quoteAsset = generateRandomString(10);
        val rate = (float) Math.random() * 100;
        val expectedResult = CurrencyRateResponse
                .builder()
                .errorCode(RateErrorCode.RATE_ALREADY_EXISTS)
                .build();

        postCurrencyRates(CurrencyRateRequest
                .builder()
                .baseAsset(baseAsset)
                .quoteAsset(quoteAsset)
                .rate(rate)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateResponse.class);

        val actualResult = postCurrencyRates(CurrencyRateRequest
                .builder()
                .baseAsset(baseAsset)
                .quoteAsset(quoteAsset)
                .rate(rate)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2644)
    void shouldUpdateCurrencyRate() {
        val baseAsset = generateRandomString(10);
        val quoteAsset = generateRandomString(10);
        val rate = (float) Math.random() * 100;
        val newRate = (float) Math.random() * 100;

        val expectedResult = CurrencyRateResponse
                .builder()
                .baseAsset(baseAsset)
                .quoteAsset(quoteAsset)
                .rate(newRate)
                .errorCode(RateErrorCode.NONE)
                .build();

        postCurrencyRates(CurrencyRateRequest
                .builder()
                .baseAsset(baseAsset)
                .quoteAsset(quoteAsset)
                .rate(rate)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateResponse.class);

        val actualResult = putCurrencyRates(CurrencyRateRequest
                .builder()
                .baseAsset(baseAsset)
                .quoteAsset(quoteAsset)
                .rate(newRate)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("invalidInputData")
    @UserStoryId(2644)
    void shouldNotCreateCurrencyRateOnWrongParameters(float rate, String baseAsset, String quoteAsset,
            ValidationErrorResponse expectedResult) {
        val actualResult = postCurrencyRates(CurrencyRateRequest
                .builder()
                .baseAsset(baseAsset)
                .quoteAsset(quoteAsset)
                .rate(rate)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("invalidInputData")
    @UserStoryId(2644)
    void shouldNotUpdateCurrencyRateOnWrongParameters(float newRate, String newBaseAsset, String newQuoteAsset,
            ValidationErrorResponse expectedResult) {
        val actualResult = putCurrencyRates(CurrencyRateRequest
                .builder()
                .baseAsset(newBaseAsset)
                .quoteAsset(newQuoteAsset)
                .rate(newRate)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("invalidInputData")
    @UserStoryId(2644)
    void shouldNotDeleteCurrencyRateOnWrongParameters(float rate, String baseAsset, String quoteAsset,
            ValidationErrorResponse expectedResult) {
        val actualResult = deleteCurrencyRates(ConverterRequest
                .builder()
                .fromAsset(baseAsset)
                .toAsset(quoteAsset)
                .build())
                .then()
                .assertThat()
                // TODO: why it's 200?
                .statusCode(SC_OK);
    }

    @Test
    @UserStoryId(2644)
    void shouldNotUpdateCurrencyRateOnWrongParameters() {
        val baseAsset = generateRandomString(10);
        val quoteAsset = generateRandomString(10);
        val rate = (float) Math.random() * 100;

        val expectedResult = CurrencyRateResponse
                .builder()
                .errorCode(RateErrorCode.RATE_DOES_NOT_EXIST)
                .build();

        val actualResult = putCurrencyRates(CurrencyRateRequest
                .builder()
                .baseAsset(baseAsset)
                .quoteAsset(quoteAsset)
                .rate(rate)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2644)
    void shouldGetCurrencyRates() {
        val baseAsset = generateRandomString(10);
        val quoteAsset = generateRandomString(10);
        val rate = (float) Math.random() * 100;
        val expectedResult = CurrencyRateModel
                .builder()
                .baseAsset(baseAsset)
                .quoteAsset(quoteAsset)
                .rate(rate)
                .build();

        val newCurrencyRate = postCurrencyRates(CurrencyRateRequest
                .builder()
                .baseAsset(baseAsset)
                .quoteAsset(quoteAsset)
                .rate(rate)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateResponse.class);

        val actualResultCollection = getCurrencyRates()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateModel[].class);

        val actualResult = Arrays.stream(actualResultCollection)
                .filter(currencyRate -> currencyRate.getBaseAsset().equalsIgnoreCase(newCurrencyRate.getBaseAsset())
                        && currencyRate.getQuoteAsset().equalsIgnoreCase(newCurrencyRate.getQuoteAsset())
                        && Math.round(currencyRate.getRate()) == Math.round(newCurrencyRate.getRate()))
                .findFirst()
                .orElse(new CurrencyRateModel());

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2644)
    void shouldDeleteCurrencyRate() {
        val baseAsset = generateRandomString(10);
        val quoteAsset = generateRandomString(10);
        val rate = (float) Math.random() * 100;
        val expectedResult = CurrencyRateModel
                .builder()
                .build();

        postCurrencyRates(CurrencyRateRequest
                .builder()
                .baseAsset(baseAsset)
                .quoteAsset(quoteAsset)
                .rate(rate)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateResponse.class);

        deleteCurrencyRates(ConverterRequest
                .builder()
                .fromAsset(baseAsset)
                .toAsset(quoteAsset)
                .build())
                .then()
                .assertThat()
                // TODO: why it's not SC_NO_CONTENT
                .statusCode(SC_OK);

        // since the deletion the currency rate should not be available
        val actualResultCollection = getCurrencyRates()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateModel[].class);

        val actualResult = Arrays.stream(actualResultCollection)
                .filter(currencyRate -> currencyRate.getBaseAsset().equalsIgnoreCase(baseAsset)
                        && currencyRate.getQuoteAsset().equalsIgnoreCase(quoteAsset)
                        && Math.round(currencyRate.getRate()) == Math.round(rate))
                .findFirst()
                .orElse(new CurrencyRateModel());

        assertEquals(expectedResult, actualResult);
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ValidationErrorResponse {

        private String[] rate;
        private String[] baseAsset;
        private String[] quoteAsset;
    }
}
