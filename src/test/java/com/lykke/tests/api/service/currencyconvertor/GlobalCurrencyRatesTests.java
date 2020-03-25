package com.lykke.tests.api.service.currencyconvertor;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.editGlobalCurrencyRate;
import static com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils.getGlobalCurrencyRate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.currencyconvertor.model.GlobalCurrencyRateModel;
import com.lykke.tests.api.service.currencyconvertor.model.GlobalCurrencyRateRequest;
import java.util.Random;
import lombok.val;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class GlobalCurrencyRatesTests extends BaseApiTest {

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2895)
    void shouldSetGlobalCurrencyRate() {
        val amountInCurrency = new Random().nextFloat();
        val amountInTokens = Double.valueOf(new Random().nextDouble() * 100);
        GlobalCurrencyRateModel expectedResult = GlobalCurrencyRateModel
                .builder()
                .amountInCurrency(amountInCurrency)
                .amountInTokens(amountInTokens.toString())
                .build();

        editGlobalCurrencyRate(GlobalCurrencyRateRequest
                .builder()
                .amountInCurrency(amountInCurrency)
                .amountInTokens(amountInTokens.toString())
                .build())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        val actualResult = getGlobalCurrencyRate()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(GlobalCurrencyRateModel.class);

        assertEquals(expectedResult, actualResult);
    }
}
