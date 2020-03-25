package com.lykke.tests.api.service.privateblockchainfacade.model.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class AgentRegisterRequestEvent {

    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneCountryName;
    private String phoneCountryCode;
    private String phoneNumber;
    private String note;
    private String countryOfResidence;
}
