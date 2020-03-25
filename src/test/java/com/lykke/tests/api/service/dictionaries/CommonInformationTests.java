package com.lykke.tests.api.service.dictionaries;

import static com.lykke.api.testing.api.common.JsonConversionUtils.convertFromJsonFile;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.HelperUtils.getTestFilePath;
import static com.lykke.tests.api.common.ResourcesConsts.Dictionaries.COMMON_INFORMATIOMN_JSON_FILE;
import static com.lykke.tests.api.service.dictionaries.DictionariesUtils.getCommonInformation;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.dictionaries.models.CommonInformationPropertiesModel;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CommonInformationTests extends BaseApiTest {

    @SneakyThrows
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {3240, 3542})
    void shouldGetCommonInformation() {
        val jsonFile = getTestFilePath(COMMON_INFORMATIOMN_JSON_FILE);

        val expectedResult = convertFromJsonFile(jsonFile, CommonInformationPropertiesModel.class);
        val actualResult = getCommonInformation();

        assertEquals(expectedResult, actualResult);
    }
}
