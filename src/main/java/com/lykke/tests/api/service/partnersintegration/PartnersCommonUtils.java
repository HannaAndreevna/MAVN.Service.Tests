package com.lykke.tests.api.service.partnersintegration;

import static com.lykke.tests.api.common.CommonConsts.Currency.SOME_CURRENCY_RATE;

import java.math.BigDecimal;
import lombok.experimental.UtilityClass;
import lombok.var;

@UtilityClass
public class PartnersCommonUtils {

    private static final Double INTERNAL_MULTIPLIER = 10_000.0;

    public Double getSendingAmountInternal(Double tokensAmount, int fiatAmount) {
        return 0.0 == tokensAmount ? fiatAmount / SOME_CURRENCY_RATE : tokensAmount;
    }

    public int getFiatAmountInternal(Double tokensAmount, float fiatAmount) {
        return roundToInt(0.0 == tokensAmount ? Double.valueOf(fiatAmount) : tokensAmount * SOME_CURRENCY_RATE, 4);
    }

    public Double getTokensAmountInternal(Double tokensAmount, float fiatAmount) {
        return roundToDouble(0.0 == tokensAmount ? Double.valueOf(fiatAmount) / SOME_CURRENCY_RATE : tokensAmount, 4);
    }

    public Double getSendingAmountExternal(Double tokensAmount, int fiatAmount) {
        return 0.0 == tokensAmount ? fiatAmount / SOME_CURRENCY_RATE : tokensAmount;
    }

    public Double getFiatAmountExternal(Double tokensAmount, float fiatAmount) {
        return roundToDouble(0.0 == fiatAmount ? tokensAmount * INTERNAL_MULTIPLIER * SOME_CURRENCY_RATE
                : Double.valueOf(fiatAmount), 4);
    }

    public Double getTokensAmountExternal(Double tokensAmount, float fiatAmount) {
        return roundToDouble(0.0 == tokensAmount ? Double.valueOf(fiatAmount) / INTERNAL_MULTIPLIER / SOME_CURRENCY_RATE
                : tokensAmount, 4);
    }

    private Double roundToDouble(Double number, int decimalPlace) {
        var bd = new BigDecimal(number);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    private int roundToInt(Double number, int decimalPlace) {
        var bd = new BigDecimal(number);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.intValue();
    }

    private Float roundToFloat(Double number, int decimalPlace) {
        var bd = new BigDecimal(number);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
