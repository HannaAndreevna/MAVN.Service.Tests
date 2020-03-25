package com.lykke.tests.api.service.tiers;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.tiers.TiersUtils.getReports;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.tiers.model.TierCustomersCountModel;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ReportsTests extends BaseApiTest {

    private static final int TIERS_NUMBER = 4;
    private static final String BLACK_TIER = "Black";
    private static final String SILVER_TIER = "Silver";
    private static final String GOLD_TIER = "Gold";
    private static final String PLATINUM_TIER = "Platinum";
    private static final Stream<String> TIER_NAMES = Stream.of(BLACK_TIER, GOLD_TIER, SILVER_TIER, PLATINUM_TIER);

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3109)
    void shouldGetReport() {
        val actualTiers = getReports()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TierCustomersCountModel[].class);

        assertAll(
                () -> assertEquals(TIERS_NUMBER, actualTiers.length),
                () -> assertEquals(TIER_NAMES.findAny().get(),
                        Arrays.stream(actualTiers).map(tier -> tier.getName()).findAny().get())
        );
    }
}
