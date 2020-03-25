package com.lykke.tests.api.service.customer.model.partnersmessages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerMessagesResponseModel {

    private String partnerMessageId;
    private String partnerId;
    private String partnerName;
    private String locationId;
    private String locationName;
    private String customerId;
    private String timeStamp;
    private String subject;
    private String message;

}
