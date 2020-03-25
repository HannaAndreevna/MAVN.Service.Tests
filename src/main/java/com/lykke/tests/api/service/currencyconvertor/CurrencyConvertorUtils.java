package com.lykke.tests.api.service.currencyconvertor;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.CurrencyConverter.CURRENCY_CONVERTER_API_PATH;
import static com.lykke.tests.api.base.Paths.CurrencyConverter.CURRENCY_RATES_API_PATH;
import static com.lykke.tests.api.base.Paths.CurrencyConverter.GLOBAL_CURRENCY_RATES_API_PATH;

import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.QueryParameters;
import com.lykke.tests.api.service.currencyconvertor.model.ConverterRequest;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateRequest;
import com.lykke.tests.api.service.currencyconvertor.model.GlobalCurrencyRateRequest;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class CurrencyConvertorUtils {

    public static final String CURRENCY_ASSET_CODE_FIELD = "BaseAsset";
    public static final String CURRENCY_ASSET_LABEL_FIELD = "QuoteAsset";
    public static final String CURRENCY_RATE_FIELD = "Rate";
    public static final String AMOUNT_FIELD = "Amount";

    Response getCurrencies() {
        return getHeader()
                .get(CURRENCY_RATES_API_PATH);
    }

    Response getCurrenciesByCurrencyCode(String currencyCode1, String currencyCode2) {
        return getHeader()
                .queryParams(getQueryParams(CurrencyRateQueryParameters
                        .builder()
                        .fromAsset(currencyCode1)
                        .toAsset(currencyCode2)
                        .build()))
                .get(CURRENCY_CONVERTER_API_PATH);
    }

    public Response createCurrencies(CurrencyRateRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(CURRENCY_RATES_API_PATH);
    }

    Response updateCurrencies(CurrencyRateRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .put(CURRENCY_RATES_API_PATH);
    }

    public Response deleteCurrenciesByCurrencyCode(String fromAsset, String toAsset) {
        return getHeader()
                .queryParams(getQueryParams(CurrencyRateQueryParameters
                        .builder()
                        .fromAsset(fromAsset)
                        .toAsset(toAsset)
                        .build()))
                .delete(CURRENCY_RATES_API_PATH);
    }

    Response getConverter(ConverterRequest requestModel) {
        return getHeader()
                .queryParams(getQueryParams(requestModel))
                .get(CURRENCY_CONVERTER_API_PATH);
    }

    Response getCurrencyRates() {
        return getHeader()
                .get(CURRENCY_RATES_API_PATH);
    }

    Response postCurrencyRates(CurrencyRateRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(CURRENCY_RATES_API_PATH);
    }

    Response putCurrencyRates(CurrencyRateRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .put(CURRENCY_RATES_API_PATH);
    }

    Response deleteCurrencyRates(ConverterRequest requestModel) {
        return getHeader()
                .queryParams(getQueryParams(requestModel))
                .delete(CURRENCY_RATES_API_PATH);
    }

    Response getGlobalCurrencyRate() {
        return getHeader()
                .get(GLOBAL_CURRENCY_RATES_API_PATH)
                .thenReturn();
    }

    Response editGlobalCurrencyRate(GlobalCurrencyRateRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .put(GLOBAL_CURRENCY_RATES_API_PATH)
                .thenReturn();
    }

    private static JSONObject convertCurrenciesObject(String currencyAssetCode, Float amount) {
        JSONObject currenciesObject = new JSONObject();
        currenciesObject.put(CURRENCY_ASSET_CODE_FIELD, currencyAssetCode);
        currenciesObject.put(AMOUNT_FIELD, amount);
        return currenciesObject;
    }

    @AllArgsConstructor
    @Builder
    @Data
    @QueryParameters
    @NetClassName("none")
    public static class CurrencyRateQueryParameters {

        private String fromAsset;
        private String toAsset;
    }
}

