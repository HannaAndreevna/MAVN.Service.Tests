package com.lykke.tests.api.service.crosschainwalletlinker;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.getConfigurationByType;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.getConfigurations;
import static com.lykke.tests.api.service.crosschainwalletlinker.CrossChainWalletLinkerUtils.postOrUpdateConfiguration;
import static com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemType.SUBSEQUENT_LINKING_FEE;
import static com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemUpdateError.NONE;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemRequestModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemResponseModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemUpdateResponseModel;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ConfigurationTests extends BaseApiTest {

    @Test
    @UserStoryId(3527)
    void shouldGetAllConfigurationItems() {
        val actualResult = getConfigurations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemResponseModel[].class);

        assertAll(
                () -> assertNotNull(actualResult),
                () -> assertNotNull((ConfigurationItemResponseModel[]) actualResult)
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3527)
    void shouldGetConfigurationItemByType() {
        val configurations = getConfigurations()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemResponseModel[].class);

        val expectedResult = configurations[configurations.length - 1];

        val actualResult = getConfigurationByType(expectedResult.getEnumType())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(3527)
    void shouldUpdateConfigurationItemByType() {
        val originalConfigurationItem = getConfigurationByType(SUBSEQUENT_LINKING_FEE)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemResponseModel.class);

        val inputData = ConfigurationItemRequestModel
                .builder()
                .type(originalConfigurationItem.getEnumType())
                .value(((Double) (Double.valueOf(originalConfigurationItem.getValue()) * 2)).toString())
                .build();

        val expectedUpdateResult = ConfigurationItemUpdateResponseModel
                .builder()
                .error(NONE)
                .build();

        val actualUpdateResult = postOrUpdateConfiguration(inputData)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemUpdateResponseModel.class);

        assertEquals(expectedUpdateResult, actualUpdateResult);

        val actualAfterUpdateResult = getConfigurationByType(originalConfigurationItem.getEnumType())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemResponseModel.class);

        assertEquals(ConfigurationItemResponseModel
                .builder()
                .type(inputData.getEnumType())
                .value(inputData.getValue())
                .build(), actualAfterUpdateResult);

        // restore the original setting
        postOrUpdateConfiguration(ConfigurationItemRequestModel
                .builder()
                .type(originalConfigurationItem.getEnumType())
                .value(originalConfigurationItem.getValue())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemUpdateResponseModel.class);

        val actualAfterRestoreResult = getConfigurationByType(originalConfigurationItem.getEnumType())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ConfigurationItemResponseModel.class);

        assertEquals(originalConfigurationItem, actualAfterRestoreResult);
    }
}
