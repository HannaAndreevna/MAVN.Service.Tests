package com.lykke.tests.api.service.currencyconvertor;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.Currency.MVN_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Currency.MVN_TO_USD_RATE;
import static com.lykke.tests.api.common.CommonConsts.Currency.USD_CURRENCY;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.getConverter;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.currencyconvertor.model.ConverterErrorCode;
import com.lykke.tests.api.service.currencyconvertor.model.ConverterRequest;
import com.lykke.tests.api.service.currencyconvertor.model.ConverterResponse;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ConverterTests extends BaseApiTest {

    static Stream<Arguments> getInvalidData() {
        return Stream.of(
                of(0, generateRandomString(10), generateRandomString(10)),
                of(-1, generateRandomString(10), generateRandomString(10)),
                of(100, EMPTY, generateRandomString(10)),
                of(100, generateRandomString(10), EMPTY),
                of(0, EMPTY, EMPTY)
        );
    }

    // TODO: could not be present in Test
    // Expected :ConverterResponse(amount=996.19, errorCode=NONE)
    // Actual   :ConverterResponse(amount=0.0, errorCode=NO_RATE)
    @Test
    @UserStoryId(2644)
    void shouldGetConverter() {
        float rate = (float) Math.random() * 100;
        val expectedResult = ConverterResponse
                .builder()
                .amount(rate * MVN_TO_USD_RATE)
                .errorCode(ConverterErrorCode.NONE)
                .build();

        val actualResult = getConverter(ConverterRequest
                .builder()
                .amount(rate)
                .fromAsset(MVN_CURRENCY)
                .toAsset(USD_CURRENCY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConverterResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2644)
    void shouldGetNoConverterOnNonExistingCurrency() {
        val expectedResult = ConverterResponse
                .builder()
                .errorCode(ConverterErrorCode.NO_RATE)
                .build();

        val actualResult = getConverter(ConverterRequest
                .builder()
                .amount(Math.random() * 100)
                .fromAsset(generateRandomString(10))
                .toAsset(generateRandomString(10))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConverterResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getInvalidData")
    @UserStoryId(2644)
    void shouldGetNoConverterOnNonExistingCurrency(float amount, String fromAsset, String toAsset) {
        val expectedResult = ConverterResponse
                .builder()
                .errorCode(ConverterErrorCode.NO_RATE)
                .build();

        val actualResult = getConverter(ConverterRequest
                .builder()
                .amount(amount)
                .fromAsset(fromAsset)
                .toAsset(toAsset)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConverterResponse.class);

        assertEquals(expectedResult, actualResult);
    }
}
