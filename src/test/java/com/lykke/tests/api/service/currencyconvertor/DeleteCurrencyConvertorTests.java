package com.lykke.tests.api.service.currencyconvertor;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.createCurrencies;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.deleteCurrenciesByCurrencyCode;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.getCurrenciesByCurrencyCode;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.currencyconvertor.model.ConverterErrorCode;
import com.lykke.tests.api.service.currencyconvertor.model.ConverterResponse;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateRequest;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateRequest.CurrencyRateRequestBuilder;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DeleteCurrencyConvertorTests extends BaseApiTest {

    private static final String CURRENCY_ASSET_CODE_01 = generateRandomString();
    private static final String CURRENCY_ASSET_LABEL = generateRandomString();
    private static final String CURRENCY_ASSET_CODE_02 = generateRandomString();
    private static final Float CURRENCY_RATE = 2.6f;
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

    @Test
    @UserStoryId(storyId = 666)
    void shouldDeleteCurrency() {
        val expectedResult = ConverterResponse
                .builder()
                .amount(0)
                .errorCode(ConverterErrorCode.NO_RATE)
                .build();

        deleteCurrenciesByCurrencyCode(CURRENCY_ASSET_CODE_01, CURRENCY_ASSET_CODE_02)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val actualResult = getCurrenciesByCurrencyCode(CURRENCY_ASSET_CODE_01, CURRENCY_ASSET_CODE_02)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConverterResponse.class);

        assertEquals(expectedResult, actualResult);
    }
}
