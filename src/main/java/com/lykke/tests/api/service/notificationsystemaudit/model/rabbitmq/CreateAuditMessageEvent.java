package com.lykke.tests.api.service.notificationsystemaudit.model.rabbitmq;

import com.lykke.tests.api.service.notificationsystemaudit.model.CallType;
import com.lykke.tests.api.service.notificationsystemaudit.model.FormattingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CreateAuditMessageEvent {

    private String timestamp;
    private String messageId;
    private Channel messageType;
    private String customerId;
    private String subjectTemplateId;
    private String messageTemplateId;
    private String source;
    private CallType callType;
    private FormattingStatus formattingStatus;
    private String formattingComment;
}

