package com.lykke.tests.api.service.dictionaries;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.Dictionaries.COUNTRIES_API_PATH;
import static com.lykke.tests.api.base.Paths.Dictionaries.COUNTRY_BY_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.Dictionaries.COUNTRY_PHONE_CODES_API_PATH;
import static com.lykke.tests.api.base.Paths.Dictionaries.COUNTRY_PHONE_CODE_BY_ID_API_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.base.Paths;
import com.lykke.tests.api.service.dictionaries.models.CommonInformationPropertiesModel;
import com.lykke.tests.api.service.dictionaries.models.salesforce.CountryOfResidenceModel;
import com.lykke.tests.api.service.dictionaries.models.salesforce.CountryPhoneCodeModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DictionariesUtils {

    CountryOfResidenceModel[] getCountriesOfResidence() {
        return getHeader()
                .get(COUNTRIES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CountryOfResidenceModel[].class);
    }

    CountryPhoneCodeModel[] getCountryPhoneCodes() {
        return getHeader()
                .get(COUNTRY_PHONE_CODES_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CountryPhoneCodeModel[].class);
    }

    public CountryOfResidenceModel getCountryOfResidence(String id) {
        return getHeader()
                .get(COUNTRY_BY_ID_API_PATH.apply(id))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CountryOfResidenceModel.class);
    }

    public CountryPhoneCodeModel getCountryPhoneCode(String id) {
        return getHeader()
                .get(COUNTRY_PHONE_CODE_BY_ID_API_PATH.apply(id))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CountryPhoneCodeModel.class);
    }

    public CommonInformationPropertiesModel getCommonInformation() {
        return getHeader()
                .get(Paths.Dictionaries.COMMON_INFORMATION_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CommonInformationPropertiesModel.class);
    }
}
