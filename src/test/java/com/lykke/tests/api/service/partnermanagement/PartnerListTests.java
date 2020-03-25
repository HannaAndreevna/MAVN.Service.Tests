package com.lykke.tests.api.service.partnermanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.partnermanagement.model.PartnerListDetailsModel;
import java.util.Arrays;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class PartnerListTests extends BaseApiTest {

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3254)
    void shouldReturnPartnersList() {
        val partnerData01 = createDefaultPartner(getRandomUuid(), generateValidPassword(), generateRandomString(10),
                generateRandomString(10));
        val partnerData02 = createDefaultPartner(getRandomUuid(), generateValidPassword(), generateRandomString(10),
                generateRandomString(10));
        val partnerData03 = createDefaultPartner(getRandomUuid(), generateValidPassword(), generateRandomString(10),
                generateRandomString(10));

        val expectedPartnersIds = new String[]{partnerData01.getId(), partnerData02.getId(), partnerData03.getId()};
        val actualPartners = PartnerManagementUtils.getpartnersList(expectedPartnersIds)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerListDetailsModel[].class);
        val actualPartnerIds = Arrays.stream(actualPartners)
                .map(partner -> partner.getId())
                .collect(toList())
                .toArray(new String[]{});

        assertArrayEquals(Arrays.stream(expectedPartnersIds).sorted().collect(toList()).toArray(new String[]{}),
                Arrays.stream(actualPartnerIds).sorted().collect(toList()).toArray(new String[]{}));
    }
}
