package com.lykke.tests.api.service.customerprofile;

import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_SEC;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customerprofile.statistics.CustomerProfileStatisticsUtils.getCustomerProfileStatistics;
import static com.lykke.tests.api.service.customerprofile.statistics.CustomerProfileStatisticsUtils.getCustomerProfileStatisticsValidationResponse;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils;
import com.lykke.tests.api.service.customerprofile.statistics.model.CustomerStatiscticsRequest;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.val;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CustomerProfileStatisticsTests extends BaseApiTest {

    static String startDateInUTC() {
        String[] startDate = OffsetDateTime.now(ZoneOffset.UTC).toString().split("T");
        return startDate[0] + "T00:00";
    }

    static Stream<Arguments> getWrongDateFormat() {
        return Stream.of(
                of("23", "12d2"),
                of("2019-05-23T06:59:26.627Z", "12d2")
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 909)
    void shouldGetCustomerProfileStatistics() {
        val VALID_START_DATE = startDateInUTC();
        val VALID_END_DATE = Instant.now().plusSeconds(60).toString();
        val requestObject = CustomerStatiscticsRequest
                .builder()
                .startDate(VALID_START_DATE)
                .endDate(VALID_END_DATE)
                .build();

        val validationResponse = getCustomerProfileStatistics(requestObject);
        val initialTotalCount = validationResponse.getTotalCount();
        val initialRegistrationsCount = validationResponse.getRegistrationsCount();

        RegisterCustomerUtils.registerCustomer();

        Awaitility.await().atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> initialTotalCount <= getCustomerProfileStatistics(requestObject).getTotalCount());
        val newResponse = getCustomerProfileStatistics(requestObject);

        // TODO: magic numbers
        assertAll(
                () -> assertEquals(initialTotalCount, newResponse.getTotalCount()),
                () -> assertEquals(initialRegistrationsCount, newResponse.getRegistrationsCount())
        );
    }

    @ParameterizedTest(name = "Run {index}: startDate={0}, endDate={1}")
    @MethodSource("getWrongDateFormat")
    @UserStoryId(storyId = 909)
    void shouldValidateGetCustomerProfileStatistics(String startDate, String endDate) {
        val requestObject = CustomerStatiscticsRequest
                .builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        val actualValidationResponse = getCustomerProfileStatisticsValidationResponse(requestObject);

        assertEquals(requestObject.getValidationResponse(), actualValidationResponse);
    }
}
