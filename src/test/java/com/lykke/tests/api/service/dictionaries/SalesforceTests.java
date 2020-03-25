package com.lykke.tests.api.service.dictionaries;

import static com.lykke.api.testing.api.common.JsonConversionUtils.convertFromJsonFile;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.HelperUtils.getTestFilePath;
import static com.lykke.tests.api.common.ResourcesConsts.Dictionaries.COUNTRIES_OF_RESIDENCE_JSON_FILE;
import static com.lykke.tests.api.common.ResourcesConsts.Dictionaries.COUNTRY_PHONE_CODES_JSON_FILE;
import static com.lykke.tests.api.service.dictionaries.DictionariesUtils.getCountriesOfResidence;
import static com.lykke.tests.api.service.dictionaries.DictionariesUtils.getCountryOfResidence;
import static com.lykke.tests.api.service.dictionaries.DictionariesUtils.getCountryPhoneCode;
import static com.lykke.tests.api.service.dictionaries.DictionariesUtils.getCountryPhoneCodes;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.dictionaries.models.salesforce.CountryOfResidenceModel;
import com.lykke.tests.api.service.dictionaries.models.salesforce.CountryPhoneCodeModel;
import java.util.Arrays;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class SalesforceTests extends BaseApiTest {

    private static final String COUNTRY_OF_RESIDENCE_ID = "197";
    private static final String COUNTRY_OF_RESIDENCE_NAME = "Singapore";
    private static final String COUNTRY_OF_RESIDENCE_ISO2_CODE = "SG";
    private static final String COUNTRY_OF_RESIDENCE_ISO3_CODE = "SGP";
    private static final String COUNTRY_PHONE_CODE_ID = "222";
    private static final String COUNTRY_PHONE_CODE_NAME = "Tonga";
    private static final String COUNTRY_PHONE_CODE_CODE = "00676";
    private static final String COUNTRY_PHONE_CODE_ISO_CODE = "+676";
    private static final String COUNTRY_PHONE_CODE_COUNTRY_ISO2_CODE = "TO";
    private static final String COUNTRY_PHONE_CODE_COUNTRY_ISO3_CODE = "TON";
    private static final CountryOfResidenceModel EXPECTED_COUNTRY_OF_RESIDENCE = CountryOfResidenceModel
            .builder()
            .id(COUNTRY_OF_RESIDENCE_ID)
            .name(COUNTRY_OF_RESIDENCE_NAME)
            .countryIso2Code(COUNTRY_OF_RESIDENCE_ISO2_CODE)
            .countryIso3Code(COUNTRY_OF_RESIDENCE_ISO3_CODE)
            .build();
    private static final CountryPhoneCodeModel EXPECTED_COUNTRY_PHONE_CODE = CountryPhoneCodeModel
            .builder()
            .id(COUNTRY_PHONE_CODE_ID)
            .countryName(COUNTRY_PHONE_CODE_NAME)
            .isoCode(COUNTRY_PHONE_CODE_ISO_CODE)
            .code(COUNTRY_PHONE_CODE_CODE)
            .countryIso2Code(COUNTRY_PHONE_CODE_COUNTRY_ISO2_CODE)
            .countryIso3Code(COUNTRY_PHONE_CODE_COUNTRY_ISO3_CODE)
            .build();

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1694, 2181})
    public void shouldGetCountriesOfResidence() {
        val countriesOfResidence = getCountriesOfResidence();
        val countryOfInterest = Arrays.stream(countriesOfResidence)
                .filter(country -> COUNTRY_OF_RESIDENCE_ID.equals(country.getId()))
                .findFirst()
                .orElse(new CountryOfResidenceModel());

        assertEquals(EXPECTED_COUNTRY_OF_RESIDENCE, countryOfInterest);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1694, 2181})
    public void shouldGetCountryPhoneCodes() {
        val countryPhoneCodes = getCountryPhoneCodes();
        val countryCodeOfInterest = Arrays.stream(countryPhoneCodes)
                .filter(code -> COUNTRY_PHONE_CODE_ID.equals(code.getId()))
                .findFirst()
                .orElse(new CountryPhoneCodeModel());

        assertEquals(EXPECTED_COUNTRY_PHONE_CODE, countryCodeOfInterest);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1694, 2181})
    public void shouldGetCountryOfResidenceById() {
        val countryOfInterest = getCountryOfResidence(COUNTRY_OF_RESIDENCE_ID);

        assertEquals(EXPECTED_COUNTRY_OF_RESIDENCE, countryOfInterest);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1694, 2181})
    public void shouldGetCountryPhoneCodeById() {
        val countryCodeOfInterest = getCountryPhoneCode(COUNTRY_PHONE_CODE_ID);

        assertEquals(EXPECTED_COUNTRY_PHONE_CODE, countryCodeOfInterest);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2179)
    @SneakyThrows
    void shouldGetFullListOfCountriesOfResidence() {
        val jsonFile = getTestFilePath(COUNTRIES_OF_RESIDENCE_JSON_FILE);

        val expectedResult = convertFromJsonFile(jsonFile, CountryOfResidenceModel[].class);
        val actualResult = getCountriesOfResidence();

        assertArrayEquals(expectedResult, actualResult);
        assertEquals(expectedResult.length, actualResult.length);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2179)
    @SneakyThrows
    void shouldGetFullListOfCountryPhoneCodes() {
        val jsonFile = getTestFilePath(COUNTRY_PHONE_CODES_JSON_FILE);

        val expectedResult = convertFromJsonFile(jsonFile, CountryPhoneCodeModel[].class);
        val actualResult = getCountryPhoneCodes();

        assertArrayEquals(expectedResult, actualResult);
        assertEquals(expectedResult.length, actualResult.length);
    }
}
