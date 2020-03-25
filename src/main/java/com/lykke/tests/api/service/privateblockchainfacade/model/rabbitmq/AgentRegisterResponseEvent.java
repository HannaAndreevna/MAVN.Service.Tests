package com.lykke.tests.api.service.privateblockchainfacade.model.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class AgentRegisterResponseEvent {

    private String customerId;
    private AgentRegisterStatus status;
    private String agentSalesforceId;
    private String registrationErrorCode;
}
