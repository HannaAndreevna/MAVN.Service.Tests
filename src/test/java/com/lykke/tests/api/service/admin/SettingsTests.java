package com.lykke.tests.api.service.admin;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.SettingsUtils.getAgentRequirements;
import static com.lykke.tests.api.service.admin.SettingsUtils.getGlobalCurrencyRate;
import static com.lykke.tests.api.service.admin.SettingsUtils.getOperationFees;
import static com.lykke.tests.api.service.admin.SettingsUtils.updateAgentRequirements;
import static com.lykke.tests.api.service.admin.SettingsUtils.updateGlobalCurrencyRate;
import static com.lykke.tests.api.service.admin.SettingsUtils.updateOperationFees;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.settings.AgentRequirementResponse;
import com.lykke.tests.api.service.admin.model.settings.AgentRequirementUpdateRequest;
import com.lykke.tests.api.service.admin.model.settings.GlobalCurrencyRateModel;
import com.lykke.tests.api.service.admin.model.settings.OperationFeesModel;
import java.util.Random;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class SettingsTests extends BaseApiTest {

    public static final int SOME_SEED = 100;

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2652)
    void shouldGetAndUpdateGlobalCurrencyRate() {
        val currentResult = getGlobalCurrencyRate()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GlobalCurrencyRateModel.class);

        val currentAmountInTokens = Double.valueOf(currentResult.getAmountInTokens());
        final Double newAmountInTokens = currentAmountInTokens + new Random(SOME_SEED).nextDouble();
        val newAmountInCurrency = currentResult.getAmountInCurrency() + new Random(SOME_SEED).nextDouble();

        val expectedResult = GlobalCurrencyRateModel
                .builder()
                .amountInTokens(newAmountInTokens.toString())
                .amountInCurrency(newAmountInCurrency)
                .build();

        updateGlobalCurrencyRate(GlobalCurrencyRateModel
                .builder()
                .amountInTokens(newAmountInTokens.toString())
                .amountInCurrency(newAmountInCurrency)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualResult = getGlobalCurrencyRate()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GlobalCurrencyRateModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2652)
    void shouldGetAndUpdateAgentRequirements() {
        val currentResult = getAgentRequirements()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AgentRequirementResponse.class);

        val currentAmount = Double.valueOf(currentResult.getTokensAmount());
        final Double newAmount = currentAmount + new Random(SOME_SEED).nextDouble();

        val expectedResult = AgentRequirementResponse
                .builder()
                .tokensAmount(newAmount.toString())
                .build();

        updateAgentRequirements(AgentRequirementUpdateRequest
                .builder()
                .tokensAmount(newAmount.toString())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualResult = getAgentRequirements()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AgentRequirementResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3732)
    void shouldGetAndUpdateOperationFees() {
        val currentResult = getOperationFees()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(OperationFeesModel.class);

        final Double newCrossChainTransferFee = Double.valueOf(currentResult.getCrossChainTransferFee()) + new Random(
                SOME_SEED * SOME_SEED).nextDouble();
        final Double newFirstTimeLinkingFee = Double.valueOf(currentResult.getFirstTimeLinkingFee()) + new Random(
                SOME_SEED).nextDouble();
        final Double newSubsequentLinkingFee = Double.valueOf(currentResult.getSubsequentLinkingFee()) + new Random(
                SOME_SEED).nextDouble();
        val updateRequestModel = OperationFeesModel
                .builder()
                .crossChainTransferFee(Double.valueOf(newCrossChainTransferFee.intValue()).toString())
                .firstTimeLinkingFee(newFirstTimeLinkingFee.toString())
                .subsequentLinkingFee(newSubsequentLinkingFee.toString())
                .build();

        updateOperationFees(updateRequestModel)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualResult = getOperationFees()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(OperationFeesModel.class);

        assertEquals(updateRequestModel, actualResult);

        // restore the original settings
        updateOperationFees(currentResult)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualRestoreResult = getOperationFees()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(OperationFeesModel.class);

        assertEquals(currentResult, actualRestoreResult);
    }
}
