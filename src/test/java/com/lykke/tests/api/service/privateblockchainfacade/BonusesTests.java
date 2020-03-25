package com.lykke.tests.api.service.privateblockchainfacade;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.postBonuses;
import static com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardRequestModel.AMOUNT_LOWER_BOUNDARY;
import static com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardRequestModel.AMOUNT_UPPER_BOUNDARY;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.DefectIds;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardError;
import com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.BonusRewardResponseModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.PrivateBlockChainFacadeCommonErrorResponseModel;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.val;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class BonusesTests extends BaseApiTest {

    private static final Double VALID_AMOUNT_01 = 0.99;
    private static final Double VALID_AMOUNT_02 = AMOUNT_LOWER_BOUNDARY;
    private static final Double VALID_AMOUNT_03 = 100.0;
    private static final Double VALID_AMOUNT_04 = AMOUNT_UPPER_BOUNDARY;
    private static final Double INVALID_AMOUNT_01 = 0.0;
    private static final Double INVALID_AMOUNT_02 = 9.22337203685479E+18;
    private static final Double INVALID_AMOUNT_03 = -AMOUNT_LOWER_BOUNDARY;
    private static final Double INVALID_AMOUNT_04 = -100.0;
    private static final Double INVALID_AMOUNT_05 = -9.22337203685411E+18;
    private static final Double INVALID_AMOUNT_06 = -AMOUNT_UPPER_BOUNDARY;
    private static final String SOME_BONUS_REASON = "some bonus reason";

    static Stream<Arguments> getInvalidAmountInputData() {
        return Stream.of(
                of(registerCustomer(), INVALID_AMOUNT_01,
                        generateRandomString(10)),
                of(registerCustomer(), INVALID_AMOUNT_02,
                        generateRandomString(10)),
                of(registerCustomer(), INVALID_AMOUNT_03,
                        generateRandomString(10)),
                of(registerCustomer(), INVALID_AMOUNT_04,
                        generateRandomString(10)),
                of(registerCustomer(), INVALID_AMOUNT_05,
                        generateRandomString(10)),
                of(registerCustomer(), INVALID_AMOUNT_06,
                        generateRandomString(10))
        );
    }

    static Stream<Arguments> getInvalidCustomerIdInputData() {
        return Stream.of(
                of("aaa", VALID_AMOUNT_01, generateValidPassword()),
                of("111", VALID_AMOUNT_01, generateValidPassword())
        );
    }

    static Stream<Arguments> getInvalidRewardIdInputData() {
        return Stream.of(
                of(registerCustomer(), VALID_AMOUNT_01, EMPTY),
                of(registerCustomer(), VALID_AMOUNT_01, null)
        );
    }

    static Stream<Arguments> getAmountData() {
        return Stream.of(
                of(VALID_AMOUNT_01),
                of(VALID_AMOUNT_02),
                of(VALID_AMOUNT_03),
                of(VALID_AMOUNT_04)
        );
    }

    @ParameterizedTest
    @MethodSource("getAmountData")
    @UserStoryId(975)
    void shouldPostValidData(Double amount) {
        val customerId = registerCustomer();
        BonusRewardResponseModel actualResult = postBonuses(
                BonusRewardRequestModel
                        .builder()
                        .customerId(customerId)
                        .amount(amount.toString())
                        .rewardRequestId(generateRandomString(10))
                        .bonusReason(SOME_BONUS_REASON)
                        .campaignId(UUID.randomUUID())
                        .conditionId(UUID.randomUUID())
                        .build())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(BonusRewardResponseModel.class);

        assertEquals(BonusRewardError.NONE, actualResult.getError());
    }

    @Test
    @UserStoryId(975)
    void shouldNotPostDuplicatedData() {
        val customerId = registerCustomer();
        val rewardRequestId = generateRandomString(10);
        BonusRewardResponseModel actualResult = postBonuses(
                BonusRewardRequestModel
                        .builder()
                        .customerId(customerId)
                        .amount(VALID_AMOUNT_04.toString())
                        .rewardRequestId(rewardRequestId)
                        .bonusReason(SOME_BONUS_REASON)
                        .campaignId(UUID.randomUUID())
                        .conditionId(UUID.randomUUID())
                        .build())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(BonusRewardResponseModel.class);

        assertEquals(BonusRewardError.NONE, actualResult.getError());

        actualResult = postBonuses(
                BonusRewardRequestModel
                        .builder()
                        .customerId(customerId)
                        .amount(VALID_AMOUNT_04.toString())
                        .rewardRequestId(rewardRequestId)
                        .bonusReason(SOME_BONUS_REASON)
                        .campaignId(UUID.randomUUID())
                        .conditionId(UUID.randomUUID())
                        .build())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(BonusRewardResponseModel.class);

        assertEquals(BonusRewardError.DUPLICATE_REQUEST, actualResult.getError());
    }

    @ParameterizedTest(name = "Run {index}: customerId={0}, amount={1}, rewardRequestId={2}")
    @MethodSource("getInvalidAmountInputData")
    @UserStoryId(storyId = {975, 1497})
    @DefectIds(defectIds = {1494, 1497})
    void shouldNotPostInvalidAmountData(String customerId, Double amount, String rewardRequestId) {
        val requestObject = BonusRewardRequestModel
                .builder()
                .customerId(customerId)
                .amount(amount.toString())
                .rewardRequestId(rewardRequestId)
                .campaignId(UUID.randomUUID())
                .conditionId(UUID.randomUUID())
                .build();
        PrivateBlockChainFacadeCommonErrorResponseModel actualResult = postBonuses(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PrivateBlockChainFacadeCommonErrorResponseModel.class);

        assertEquals(requestObject.getAmountMessage()[0], actualResult.getModelErrors().getAmount()[0]);
    }

    @Test
    @UserStoryId(storyId = {975, 1508})
    @DefectIds(1508)
    void shouldNotPostInvalidCustomerIdData() {
        val requestObject = BonusRewardRequestModel
                .builder()
                .customerId(EMPTY)
                .amount(VALID_AMOUNT_01.toString())
                .rewardRequestId(generateRandomString(10))
                .build();
        PrivateBlockChainFacadeCommonErrorResponseModel actualResult = postBonuses(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PrivateBlockChainFacadeCommonErrorResponseModel.class);

        assertEquals(requestObject.getCustomerIdMessage()[0], actualResult.getCustomerId()[0]);
    }

    @ParameterizedTest(name = "Run {index}: customerId={0}, amount={1}, rewardRequestId={2}")
    @MethodSource("getInvalidCustomerIdInputData")
    @UserStoryId(storyId = {975, 1508})
    @DefectIds(1508)
    void shouldPostInvalidCustomerIdData(String customerId, Double amount, String rewardRequestId) {
        val requestObject = BonusRewardRequestModel
                .builder()
                .customerId(customerId)
                .amount(amount.toString())
                .rewardRequestId(rewardRequestId)
                .build();
        PrivateBlockChainFacadeCommonErrorResponseModel actualResult = postBonuses(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PrivateBlockChainFacadeCommonErrorResponseModel.class);

        assertEquals(requestObject.getCustomerIdMessage()[0], actualResult.getCustomerId()[0]);
    }

    @ParameterizedTest(name = "Run {index}: customerId={0}, amount={1}, rewardRequestId={2}")
    @MethodSource("getInvalidRewardIdInputData")
    @UserStoryId(storyId = {975, 1508})
    @DefectIds(1508)
    void shouldPostInvalidRewardIdData(String customerId, Double amount, String rewardRequestId) {
        val requestObject = BonusRewardRequestModel
                .builder()
                .customerId(customerId)
                .amount(amount.toString())
                .rewardRequestId(rewardRequestId)
                .campaignId(UUID.randomUUID())
                .conditionId(UUID.randomUUID())
                .build();
        PrivateBlockChainFacadeCommonErrorResponseModel actualResult = postBonuses(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PrivateBlockChainFacadeCommonErrorResponseModel.class);

        assertEquals(requestObject.getRewardRequestIdMessage()[0], actualResult.getRewardRequestId()[0]);
    }
}
