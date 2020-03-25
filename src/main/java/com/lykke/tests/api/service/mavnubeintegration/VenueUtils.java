package com.lykke.tests.api.service.mavnubeintegration;

import static com.lykke.tests.api.base.PathConsts.MAVNUbeIntegrationService.VENUE;
import static com.lykke.tests.api.base.Paths.MVN_UBE_INTEGRATION_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class VenueUtils {
    private static final String ID = "Id";
    private static final String NAME = "Name";
    private static final String ADDRESS = "Address";
    private static final String LOCATION = "Location";
    private static final String VENUE_TYPE = "VenueType";

    public Response addVenue(Venue venue) {
        return getHeader()
                .body(venueObject(venue))
                .post(MVN_UBE_INTEGRATION_API_PATH + VENUE.getPath());
    }

    private static JSONObject venueObject(Venue venue) {
        JSONObject venueObject = new JSONObject();
        venueObject.put(ID, venue.getId());
        venueObject.put(NAME, venue.getName());
        venueObject.put(ADDRESS, venue.getAddress());
        venueObject.put(LOCATION, venue.getLocation());
        venueObject.put(VENUE_TYPE, venue.getVenueType());
        return venueObject;
    }
}
