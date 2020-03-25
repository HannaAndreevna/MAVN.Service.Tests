package com.lykke.tests.api.service.currencyconvertor;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.createCurrencies;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateRequest;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateRequest.CurrencyRateRequestBuilder;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.Arguments;

public class ConvertCurrencyConvertorTests extends BaseApiTest {

    private static final String BASE_ASSET_CODE = "MAVNToken";
    private static final String CURRENCY_ASSET_CODE_FIELD = "CurrencyAssetCode";
    private static final String CURRENCY_ASSET_CODE = generateRandomString();
    private static final String CURRENCY_ASSET_LABEL = generateRandomString();
    private static final Float CURRENCY_RATE = 5f;
    private static final Float amount = 7f;
    private static final String ERROR_CODE_FIELD = "ErrorCode";
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final String CURRENCY_NOT_FOUND_MESSAGE = "CurrencyNotFound";
    private static final String CURRENCY_ASSET_CODE_0_FIELD = "CurrencyAssetCode[0]";
    private static final String CURRENCY_ASSET_CODE_1_FIELD = "CurrencyAssetCode[1]";
    private static final String AMOUNT_0_FIELD = "Amount[0]";
    private static final String CURRENCY_ASSET_CODE_NUST_NOT_BE_EMPTY_MESSAGE = "'Currency Asset Code' must not be empty.";
    private static final String CURRENCY_ASSET_CODE_NUST_BE_AT_LEAST_3_CHARACTERS_MESSAGE = "The length of 'Currency Asset Code' must be at least 3 characters. You entered 0 characters.";
    private static final String CURRENCY_ASSET_CODE_NUST_BE_AT_LEAST_3_CHARACTERS_2_MESSAGE = "The length of 'Currency Asset Code' must be at least 3 characters. You entered 2 characters.";
    private static final String INVALID_INPUT_MESSAGE = "The input was not valid.";
    private static final String AMOUNT_MUST_BE_GREATER_THAN_OR_EQUAL_TO_0_MESSAGE = "'Amount' must be greater than or equal to '0'.";
    private static final String NAME_FIELD = "name";
    private static CurrencyRateRequest currencyConvertor;
    private static CurrencyRateRequestBuilder baseCurrencyConvertor;
    private static Float MVN_TOKEN_AMOUNT = CURRENCY_RATE * amount;

    @BeforeAll
    static void setup() {
        baseCurrencyConvertor = CurrencyRateRequest
                .builder()
                .baseAsset(CURRENCY_ASSET_CODE)
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
                        EMPTY,
                        amount,
                        CURRENCY_ASSET_CODE_0_FIELD,
                        CURRENCY_ASSET_CODE_NUST_NOT_BE_EMPTY_MESSAGE),
                of(
                        EMPTY,
                        amount,
                        CURRENCY_ASSET_CODE_1_FIELD,
                        CURRENCY_ASSET_CODE_NUST_BE_AT_LEAST_3_CHARACTERS_MESSAGE),
                of(
                        null,
                        amount,
                        CURRENCY_ASSET_CODE_0_FIELD,
                        CURRENCY_ASSET_CODE_NUST_NOT_BE_EMPTY_MESSAGE),
                of(
                        "sa",
                        amount,
                        CURRENCY_ASSET_CODE_0_FIELD,
                        CURRENCY_ASSET_CODE_NUST_BE_AT_LEAST_3_CHARACTERS_2_MESSAGE),
                of(
                        CURRENCY_ASSET_CODE,
                        null,
                        AMOUNT_0_FIELD,
                        INVALID_INPUT_MESSAGE),
                of(
                        CURRENCY_ASSET_CODE,
                        -1f,
                        AMOUNT_0_FIELD,
                        AMOUNT_MUST_BE_GREATER_THAN_OR_EQUAL_TO_0_MESSAGE)
        );
    }
}
