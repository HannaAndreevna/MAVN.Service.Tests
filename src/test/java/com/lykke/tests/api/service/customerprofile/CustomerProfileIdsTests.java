package com.lykke.tests.api.service.customerprofile;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.generateNumberOfCustomerProfiles;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.getCustomerProfilesByIds;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customerprofile.model.CustomerProfile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CustomerProfileIdsTests extends BaseApiTest {

    private static final String SOME_EMAIL_01 = "some@email1";
    private static final String SOME_EMAIL_02 = "some@email2";
    private static final String INVALID_ID_01 = "someemail1";
    private static final String INVALID_ID_02 = "222";

    @Disabled("TODO: needs investigation")
    @ParameterizedTest(name = "Run {index}: {0} customer profile(s)")
    @ValueSource(ints = {1, 3, 10})
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1057, 3770})
    void shouldGetCustomerProfilesByIds(int number) {
        val customersData = generateNumberOfCustomerProfiles(number);
        val actualCustomerProfiles = getCustomerProfilesByIds(customersData);

        assertAll(
                () -> assertEquals(customersData.keySet().size(), actualCustomerProfiles.length),
                () -> assertTrue(compareIds(customersData, actualCustomerProfiles))
        );
    }

    @Test
    @UserStoryId(storyId = {1057, 3770})
    void shouldNotGetCustomerProfilesByNoIds() {
        val customersData = new HashMap<String, String>();
        val actualCustomerProfiles = getCustomerProfilesByIds(customersData);

        assertEquals(customersData.keySet().size(), actualCustomerProfiles.length);
    }

    @Test
    @UserStoryId(storyId = {1057, 3770})
    void shouldNotGetCustomerProfilesByWrongIds() {
        val customersData = Stream.of(new String[][]{
                {SOME_EMAIL_01, INVALID_ID_01},
                {SOME_EMAIL_02, INVALID_ID_02}
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
        val actualCustomerProfiles = getCustomerProfilesByIds(customersData);

        assertEquals(0, actualCustomerProfiles.length);
    }

    @Test
    @UserStoryId(storyId = {1057, 3770})
    void shouldNotGetCustomerProfilesByNonExistingIds() {
        val customersData = Stream.of(new String[][]{
                {SOME_EMAIL_01, UUID.randomUUID().toString()},
                {SOME_EMAIL_02, UUID.randomUUID().toString()}
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
        val actualCustomerProfiles = getCustomerProfilesByIds(customersData);

        assertEquals(0, actualCustomerProfiles.length);
    }

    private boolean compareIds(Map<String, String> expectedIds, CustomerProfile[] actualIds) {
        val expected = expectedIds.values()
                .toArray(new String[]{});
        val actual = Arrays.stream(actualIds)
                .map(CustomerProfile::getCustomerId)
                .collect(toList())
                .toArray(new String[]{});
        Arrays.sort(expected);
        Arrays.sort(actual);
        return IntStream.range(0, expected.length)
                .anyMatch(item -> expected[item].equals(actual[item]));
    }
}
