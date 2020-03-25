package com.lykke.tests.api.service.pushnotifications.model.notificationmessages;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.api.testing.annotations.QueryParameters;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
@QueryParameters
public class NotificationMessagesRequestModel extends PaginatedRequestModel {

    private String customerId;

    @Builder(builderMethodName = "notificationMessagesRequestModelBuilder")
    public NotificationMessagesRequestModel(int currentPage, int pageSize, String customerId) {
        super(currentPage, pageSize);
        this.customerId = customerId;
    }
}
