package com.lykke.tests.api.service.mavnubeintegration;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Venue {
    private UUID id;
    private String name;
    private String address;
    private String location;
    private String venueType;
}
