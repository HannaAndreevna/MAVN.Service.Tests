package com.lykke.tests.api.service.tiers;

import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.tiers.TiersUtils.getCustomerTier;
import static com.lykke.tests.api.service.tiers.TiersUtils.getTiers;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.tiers.model.CustomerTierResponseErrorCode;
import com.lykke.tests.api.service.tiers.model.CustomerTierResponseModel;
import com.lykke.tests.api.service.tiers.model.TierModel;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CustomersTests extends BaseApiTest {

    private static final String BLACK_TIER = "Black";
    private static final String CUSTOMER_NOT_FOUND_ERROR_MESSAGE = "CustomerNotFound";
    private static final String NONE_ERROR_MESSAGE = "None";

    static Stream<Arguments> getNonExistingCustomerData() {
        return Stream.of(
                of("a newly registered customer",
                        registerDefaultVerifiedCustomer().getCustomerId(),
                        CustomersTiersErrorResponseModel
                                .builder()
                                .errorCode(NONE_ERROR_MESSAGE)
                                .build()),
                of("non-existing customer", getRandomUuid(), CustomersTiersErrorResponseModel
                        .builder()
                        .errorCode(CUSTOMER_NOT_FOUND_ERROR_MESSAGE)
                        .build())
        );
    }

    @SneakyThrows
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3109)
    void shouldGetCustomerTier() {
        val customerInfo = registerDefaultVerifiedCustomer();

        Awaitility.await()
                .atMost(Duration.TEN_SECONDS.plus(Duration.TEN_SECONDS))
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> SC_OK == getCustomerTier(customerInfo.getCustomerId())
                        .then()
                        .assertThat()
                        .extract()
                        .statusCode());

        val tiersCollection = getTiers()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TierModel[].class);
        val expectedResult = CustomerTierResponseModel
                .builder()
                .errorCode(CustomerTierResponseErrorCode.NONE)
                .tier(
                        Arrays.stream(tiersCollection)
                                .filter(tier -> BLACK_TIER.equalsIgnoreCase(tier.getName()))
                                .findFirst()
                                .orElse(new TierModel()))
                .build();

        val actualCustomerTier = getCustomerTier(customerInfo.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerTierResponseModel.class);

        assertEquals(expectedResult, actualCustomerTier);
    }

    @ParameterizedTest(name = "Run {index}: type={0}, customer id={1}")
    @MethodSource("getNonExistingCustomerData")
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {3109, 3262, 3350})
    void shouldNotGetCustomerTier(String type, String customerId, CustomersTiersErrorResponseModel expectedResult) {
        val actualResult = getCustomerTier(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomersTiersErrorResponseModel.class);

        assertEquals(expectedResult.getErrorCode(), actualResult.getErrorCode());
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CustomersTiersErrorResponseModel {

        private String errorCode;
        private TierModel tier;
    }
}
