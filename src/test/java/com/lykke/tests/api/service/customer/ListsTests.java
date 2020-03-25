package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.JsonConversionUtils.convertFromJsonFile;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.HelperUtils.getTestFilePath;
import static com.lykke.tests.api.common.ResourcesConsts.CustomerApi.COUNTRIES_OF_RESIDENCE_JSON_FILE;
import static com.lykke.tests.api.common.ResourcesConsts.CustomerApi.COUNTRY_PHONE_CODES_JSON_FILE;
import static com.lykke.tests.api.service.customer.ListsUtils.getListOfCountriesOfResidence;
import static com.lykke.tests.api.service.customer.ListsUtils.getListOfCountryPhoneCodes;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customer.model.lists.CountriesOfResidenceResponse;
import com.lykke.tests.api.service.customer.model.lists.CountryPhoneCodesResponse;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ListsTests extends BaseApiTest {

    @Test
    @UserStoryId(storyId = {1554, 2179})
    @Tag(SMOKE_TEST)
    @SneakyThrows
    void shouldGetListOfCountriesOfResidence() {
        val jsonFile = getTestFilePath(COUNTRIES_OF_RESIDENCE_JSON_FILE);

        val expectedResult = convertFromJsonFile(jsonFile, CountriesOfResidenceResponse[].class);
        val actualResult = getListOfCountriesOfResidence();

        assertArrayEquals(expectedResult, actualResult);
        assertEquals(expectedResult.length, actualResult.length);
    }

    @Test
    @UserStoryId(storyId = {1554, 2179})
    @Tag(SMOKE_TEST)
    @SneakyThrows
    void shouldGetCountryPhoneCodes() {
        val jsonFile = getTestFilePath(COUNTRY_PHONE_CODES_JSON_FILE);

        val expectedResult = convertFromJsonFile(jsonFile, CountryPhoneCodesResponse[].class);
        val actualResult = getListOfCountryPhoneCodes();

        assertArrayEquals(expectedResult, actualResult);
        assertEquals(expectedResult.length, actualResult.length);
    }
}
