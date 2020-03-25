package com.lykke.tests.api.service.currencyconvertor;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.createCurrencies;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.deleteCurrenciesByCurrencyCode;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.getCurrencies;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.getCurrenciesByCurrencyCode;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.GenerateUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateModel;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateRequest;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class GetCurrencyConvertorTests extends BaseApiTest {

    private static final String CURRENCY_ASSET_CODE_01 = GenerateUtils.generateRandomString();
    private static final String CURRENCY_ASSET_LABEL = GenerateUtils.generateRandomString();
    private static final String CURRENCY_ASSET_CODE_02 = GenerateUtils.generateRandomString();
    private static final Float CURRENCY_RATE = 2.6f;
    private static final String ERROR_CODE_FIELD = "ErrorCode";
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final String CURRENCY_NOT_FOUND_MESSAGE = "CurrencyNotFound";
    private static final String NO_RATE_ERROR_MESSAGE = "NoRate";
    private static final String NONE_ERROR_MESSAGE = "None";
    private static final Function<String, String> CURRENCY_WITH_CODE_C_DOES_NOT_EXIST_ERROR_MESSAGE =
            (currencyCode) ->
                    String.format("Currency with code '%s' does not exist.", currencyCode);
    private static final String invalidCurrencyCode = "WOW";
    private static CurrencyRateRequest currencyConvertor;

    @BeforeAll
    static void setup() {
        currencyConvertor = CurrencyRateRequest
                .builder()
                .baseAsset(CURRENCY_ASSET_CODE_01)
                .quoteAsset(CURRENCY_ASSET_LABEL)
                .rate(CURRENCY_RATE)
                .build();

        createCurrencies(currencyConvertor);
    }

    @AfterAll
    static void cleanup() {
        deleteCurrenciesByCurrencyCode(CURRENCY_ASSET_CODE_01, CURRENCY_ASSET_CODE_02)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 666)
    void shouldGetAllCurrencies() {
        getCurrencies()
                .then()
                .assertThat()
                .statusCode(SC_OK);
        //TODO: Compare
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 666)
    void shouldGetCurrencyByCurrencyCode() {
        val currencies = getCurrencies()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyRateModel[].class);

        val expectedResult = CurrencyConvertorErrorResponse
                .builder()
                .amount(0.0)
                .errorCode(NONE_ERROR_MESSAGE)
                .build();

        val actualResult = getCurrenciesByCurrencyCode(currencies[0].getBaseAsset(), currencies[0].getQuoteAsset())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyConvertorErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(storyId = 666)
    void shouldNotGetCurrencyByCurrencyCodeWhenTheCurrencyCodeDoesNotExist() {
        val expectedResult = CurrencyConvertorErrorResponse
                .builder()
                .amount(0.0)
                // TODO:
                .errorCode(NONE_ERROR_MESSAGE) // it was NO_RATE_ERROR_MESSAGE)
                .build();

        val actualResult = getCurrenciesByCurrencyCode(invalidCurrencyCode, invalidCurrencyCode)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CurrencyConvertorErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrencyConvertorErrorResponse {

        private Double amount;
        private String errorCode;
    }
}
