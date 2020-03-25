package com.lykke.tests.api.service.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDetailsResponse {

    private String customerId;
    private String email;
    private String firstName;
    private String lastName;
    private List<CustomerParticipatedCampaignRowModel> participatedCampaings;
    private Date registeredDate;
}
