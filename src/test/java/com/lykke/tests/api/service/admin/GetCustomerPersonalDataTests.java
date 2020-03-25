package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.service.admin.GetCustomerPersonalDataUtils.CUSTOMER_NOT_FOUND_ERROR_CODE;
import static com.lykke.tests.api.service.admin.GetCustomerPersonalDataUtils.CUSTOMER_NOT_FOUND_ERROR_MESSAGE;
import static com.lykke.tests.api.service.admin.GetCustomerPersonalDataUtils.getCustomerDetails;
import static com.lykke.tests.api.service.admin.GetCustomerPersonalDataUtils.getCustomerDetails_Unsuccessful;
import static com.lykke.tests.api.service.admin.GetCustomerPersonalDataUtils.getCustomerPersonalData;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.referral.ReferralCodeUtils.getReferralCode;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.CustomerParticipatedCampaignRowModel;
import com.lykke.tests.api.service.admin.model.PersonalDataResponse;
import com.lykke.tests.api.service.campaigns.model.CampaignResponseModel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import io.qameta.allure.Step;
import lombok.val;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class GetCustomerPersonalDataTests extends BaseApiTest {

    private static final String ERROR_FIELD = "error";
    private static final String MESSAGE_FIELD = "message";
    private static final String ERROR = "CustomerNotFound";
    private static final String ERROR_MESSAGE = "Customer not found.";
    private static final String NOT_AGENT_STATUS = "NotAgent";
    private static final int REFERRAL_CODE_LENGTH = 6;
    private static String token;

    @BeforeEach
    void setup() {
        token = getAdminToken();
    }

    // TODO: unrecognized field
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 514)
    void shouldGetPersonalDetailsForValidCustomerId() {
        val customerId = registerCustomer();

        val actualPersonalData = getActualCustomerPersonalData(customerId);

        assertAll(
                () -> assertEquals(0, actualPersonalData.getCustomerStatus()),
                () -> assertEquals(0, actualPersonalData.getWalletStatus()),
                () -> assertNotNull(actualPersonalData.getReferralCode()),
                () -> assertEquals(REFERRAL_CODE_LENGTH, actualPersonalData.getReferralCode().length()),
                () -> assertEquals(getReferralCode(customerId), actualPersonalData.getReferralCode()),
                () -> assertNotNull(actualPersonalData.getRegisteredDate()),
                () -> assertEquals(NOT_AGENT_STATUS, actualPersonalData.getAgentStatus())
        );
    }

    @Test
    @UserStoryId(storyId = 514)
    void shouldNotGetPersonalDetailsForInvalidCustomerId() {
        val customerId = getRandomUuid();

        getCustomerPersonalData(token, customerId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(ERROR))
                .body(MESSAGE_FIELD, equalTo(ERROR_MESSAGE));
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 646)
    void shouldGetDetailsForValidCustomerId() {
        val customerId = registerCustomer();

        val actualCustomerDetails = getCustomerDetails(token, customerId);

        assertNotNull(actualCustomerDetails);
    }

    @Test
    @UserStoryId(storyId = 646)
    void shouldNotGetDetailsForInvalidCustomerId() {
        String customerId = getRandomUuid();

        val actualCustomerDetails = getCustomerDetails_Unsuccessful(token, customerId);

        assertAll(
                () -> assertEquals(CUSTOMER_NOT_FOUND_ERROR_CODE, actualCustomerDetails.getError()),
                () -> assertEquals(CUSTOMER_NOT_FOUND_ERROR_MESSAGE, actualCustomerDetails.getMessage())
        );
    }

    @Step("Get Actual Personal Data")
    private PersonalDataResponse getActualCustomerPersonalData(String customerId) {
        return getCustomerPersonalData(token, customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PersonalDataResponse.class);
    }

    private List<String> getCampaignsIds(List<CampaignResponseModel> campaigns) {
        return campaigns
                .stream()
                .filter(distinctByKey(c -> c.getName() + c.getId()))
                .map(campaign -> campaign.getId())
                .collect(toList());
    }

    private boolean compareIds(List<String> expectedIds, List<CustomerParticipatedCampaignRowModel> actualIds) {
        val expected = expectedIds
                .toArray(new String[]{});
        val actual = actualIds
                .stream()
                .map(model -> model.getCampaignId())
                .collect(toList())
                .toArray(new String[]{});
        Arrays.sort(expected);
        Arrays.sort(actual);
        return IntStream.range(0, expected.length)
                .anyMatch(item -> expected[item].equals(actual[item]));
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
