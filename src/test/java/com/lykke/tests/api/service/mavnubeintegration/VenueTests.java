package com.lykke.tests.api.service.mavnubeintegration;

import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.api.testing.api.common.GenerateUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;

public class VenueTests extends BaseApiTest {

    private static UUID id = UUID.randomUUID();
    private static String name = GenerateUtils.generateName(10);
    private static String address = generateRandomString();
    private static String location = generateRandomString();
    private static String venueType = "Hotel"; //Hardcoded for time being

    @Test
    @Disabled("Endpoint currently not active")
    void shouldAddVenue() {
        /*VenueUtils.addVenue(id, name, address, location, venueType)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);*/
    }
}
