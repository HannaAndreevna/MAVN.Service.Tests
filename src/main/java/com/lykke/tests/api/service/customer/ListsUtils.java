package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.base.Paths.Customer.LIST_COUNTRIES_OF_RESIDENCE_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.LIST_COUNTRY_PHONE_CODES_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.customer.model.lists.CountriesOfResidenceResponse;
import com.lykke.tests.api.service.customer.model.lists.CountryPhoneCodesResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ListsUtils {

    public CountriesOfResidenceResponse[] getListOfCountriesOfResidence() {
        return getHeader()
                .get(LIST_COUNTRIES_OF_RESIDENCE_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CountriesOfResidenceResponse[].class);
    }

    public CountryPhoneCodesResponse[] getListOfCountryPhoneCodes() {
        return getHeader()
                .get(LIST_COUNTRY_PHONE_CODES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CountryPhoneCodesResponse[].class);
    }
}
