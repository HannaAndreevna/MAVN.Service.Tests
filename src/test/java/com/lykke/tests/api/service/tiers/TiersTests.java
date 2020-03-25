package com.lykke.tests.api.service.tiers;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.tiers.TiersUtils.getTierById;
import static com.lykke.tests.api.service.tiers.TiersUtils.getTiers;
import static com.lykke.tests.api.service.tiers.TiersUtils.putTier;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.tiers.model.TierModel;
import java.util.Arrays;
import java.util.Random;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class TiersTests extends BaseApiTest {

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3109)
    void shouldGetTiers() {
        val actualResult = getTiers()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TierModel[].class);

        assertTrue(0 <= actualResult.length);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3109)
    void shouldGetTierById() {
        val tiersCollection = getTiers()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TierModel[].class);
        val expectedResult = Arrays.stream(tiersCollection)
                .findAny()
                .orElse(new TierModel());

        val actualResult = getTierById(expectedResult.getId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TierModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Disabled("not to change the value")
    @Test
    @UserStoryId(3109)
    void shouldUpdateTier() {
        final Integer newThreshold = new Random(1).nextInt(100);

        val tiersCollection = getTiers()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TierModel[].class);
        val tierToUpdate = Arrays.stream(tiersCollection)
                .findAny()
                .orElse(new TierModel());

        // TODO: not to change name
        // tierToUpdate.setName(SOME_NEW_NAME);
        tierToUpdate.setThreshold(newThreshold.toString());

        putTier(tierToUpdate)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val actualResult = getTierById(tierToUpdate.getId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TierModel.class);

        assertEquals(newThreshold.toString(), actualResult.getThreshold());
    }

    @Test
    @UserStoryId(3501)
    void shouldNotUpdateTierWithZeroThreshold() {
        final Integer newThreshold = 0;

        val tiersCollection = getTiers()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TierModel[].class);
        val tierToUpdate = Arrays.stream(tiersCollection)
                .findAny()
                .orElse(new TierModel());

        tierToUpdate.setThreshold(newThreshold.toString());

        putTier(tierToUpdate)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }
}
