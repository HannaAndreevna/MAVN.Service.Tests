package com.lykke.tests.api.service.notificationsystemaudit;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.BuilderUtils.getObjectWithData;
import static com.lykke.tests.api.base.Paths.NotificationSystemAudit.FAILED_DELIVERY_API_PATH;
import static com.lykke.tests.api.base.Paths.NotificationSystemAudit.MESSAGE_API_PATH;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.notificationsystemaudit.model.AuditMessageResponseModel;
import com.lykke.tests.api.service.notificationsystemaudit.model.AuditMessageWithTemplateIssuesPaginatedRequestModel;
import com.lykke.tests.api.service.notificationsystemaudit.model.DeliveryFailedAuditMessageResponseModel;
import com.lykke.tests.api.service.notificationsystemaudit.model.DeliveryStatus;
import com.lykke.tests.api.service.notificationsystemaudit.model.MessageType;
import com.lykke.tests.api.service.notificationsystemaudit.model.PaginatedAuditMessageRequestModel;
import com.lykke.tests.api.service.notificationsystemaudit.model.PaginatedAuditMessageResponseModel;
import com.lykke.tests.api.service.notificationsystemaudit.model.PaginatedDeliveryFailedAuditMessageRequestModel;
import com.lykke.tests.api.service.notificationsystemaudit.model.PaginatedDeliveryFailedAuditMessageResponseModel;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class NotificationSystemAuditUtils {

    public AuditMessageResponseModel getAuditMessageFromService(
            String messageId,
            String customerIdParam,
            Consumer<PaginatedAuditMessageRequestModel>... actions) {
        val response = getHeader(getAdminToken())
                .body(getAuditMessageRequestObject(actions))
                .post(MESSAGE_API_PATH)
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedAuditMessageResponseModel.class);
        return getMessageObject(response, customerIdParam, messageId);
    }

    public AuditMessageResponseModel getAuditMessageWithTemplateIssuesFromService(
            String messageId,
            String customerIdParam,
            Consumer<AuditMessageWithTemplateIssuesPaginatedRequestModel>... actions) {
        val response = getHeader(getAdminToken())
                .body(getAuditMessageWithTemplateIssuesRequestObject(actions))
                .post(MESSAGE_API_PATH)
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedAuditMessageResponseModel.class);
        return getMessageObject(response, customerIdParam, messageId);
    }

    public DeliveryFailedAuditMessageResponseModel getAuditFailedMessageFromService(
            String messageId,
            String customerIdParam,
            Consumer<PaginatedDeliveryFailedAuditMessageRequestModel>... actions) {
        val response = getHeader(getAdminToken())
                .body(getFailedAuditMessageRequestObject(actions))
                .post(MESSAGE_API_PATH)
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedDeliveryFailedAuditMessageResponseModel.class);
        return getMessageObject(response, customerIdParam, messageId);
    }

    public AuditMessageResponseModel getAuditMessageFromService(
            String messageId,
            String customerIdParam,
            MessageType messageType,
            DeliveryStatus deliveryStatus,
            Consumer<PaginatedAuditMessageRequestModel>... actions) {

        Consumer<PaginatedAuditMessageRequestModel>[] standardActions = new Consumer[]{};
        val standardActionsStream = Stream
                .of(////55x -> x.setCustomerId(customerIdParam),
                        x -> x.setMessageType(messageType.getType()),
                        (Consumer<PaginatedAuditMessageRequestModel>) (x -> x
                                .setDeliveryStatus(deliveryStatus.getStatus()))
                );
        val allActions = combineActions(standardActionsStream, actions);
        val response = getHeader(getAdminToken())
                .body(getAuditMessageRequestObject(allActions))
                .post(MESSAGE_API_PATH)
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedAuditMessageResponseModel.class);
        return getMessageObject(response, customerIdParam, messageId);
    }

    public AuditMessageResponseModel getAuditMessageFromService(String customerId, MessageType messageType,
            DeliveryStatus deliveryStatus, String templateId) {
        val outputCollection = getHeader(getAdminToken())
                ////5555+
                .body(PaginatedAuditMessageRequestModel
                        .builder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(500)
                        .customerId(customerId)
                        .deliveryStatus(deliveryStatus.getStatus())
                        .messageType(messageType.getType())
                        .build())
                .post(MESSAGE_API_PATH)
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedAuditMessageResponseModel.class);
        return null == outputCollection.getAuditMessages() || 0 == outputCollection.getAuditMessages().size()
                ? new AuditMessageResponseModel()
                : outputCollection.getAuditMessages().stream()
                        .filter(message -> message.getMessageTemplateId().equalsIgnoreCase(templateId))
                        .findFirst()
                        .orElse(new AuditMessageResponseModel());
    }

    private AuditMessageResponseModel getMessageObject(PaginatedAuditMessageResponseModel messageCollection,
            String customerId, String messageId) {

        ////55     val candidateAuditMessage = messageCollection.getAuditMessages()
        return messageCollection.getAuditMessages()
                .stream()
                .filter(msg -> ////55customerId.equals(msg.getCustomerId())
                                ////55messageId.equalsIgnoreCase(msg.getMessageId())
                                null != messageId && !EMPTY.equals(messageId) ? messageId.equalsIgnoreCase(msg.getMessageId())
                                        : true
                        ////55       && 0 > startTestTimestamp
                        ////55       .compareTo(msg.getCreationTimestamp()))
                )
                .findFirst() ////55;
                .orElse(new AuditMessageResponseModel());
        ////55
        /*
        return candidateAuditMessage.isPresent() ? candidateAuditMessage.get()
                : candidateAuditMessage.orElseGet(() -> new AuditMessageResponseModel());
        */
    }

    private DeliveryFailedAuditMessageResponseModel getMessageObject(
            PaginatedDeliveryFailedAuditMessageResponseModel messageCollection,
            String customerId, String messageId) {

        ////55     val candidateAuditMessage = messageCollection.getAuditMessages()
        return messageCollection.getAuditMessages()
                .stream()
                .filter(msg -> ////55customerId.equals(msg.getCustomerId())
                                ////55messageId.equalsIgnoreCase(msg.getMessageId())
                                null != messageId && !EMPTY.equals(messageId) ? messageId.equalsIgnoreCase(msg.getMessageId())
                                        : true
                        ////55       && 0 > startTestTimestamp
                        ////55       .compareTo(msg.getCreationTimestamp()))
                )
                .findFirst() ////55;
                .orElse(new DeliveryFailedAuditMessageResponseModel());
        ////55
        /*
        return candidateAuditMessage.isPresent() ? candidateAuditMessage.get()
                : candidateAuditMessage.orElseGet(() -> new AuditMessageResponseModel());
        */
    }

    private Consumer<PaginatedAuditMessageRequestModel>[] combineActions(
            Stream<Consumer<PaginatedAuditMessageRequestModel>> standardActionsStream,
            Consumer<PaginatedAuditMessageRequestModel>[] actions) {

        Consumer<PaginatedAuditMessageRequestModel>[] additionalActions = new Consumer[]{};
        Consumer<PaginatedAuditMessageRequestModel>[] allActions = null != actions && 0 < actions.length
                ? Stream.concat(standardActionsStream, Arrays.stream(actions))
                .collect(toList())
                .toArray(additionalActions)
                : standardActionsStream
                        .collect(toList())
                        .toArray(additionalActions);
        return allActions;
    }

    private PaginatedAuditMessageRequestModel getAuditMessageRequestObject(
            Consumer<PaginatedAuditMessageRequestModel>... actions) {
        return getObjectWithData(
                PaginatedAuditMessageRequestModel
                        .builder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(50)
                        .build(),
                actions);
    }

    private AuditMessageWithTemplateIssuesPaginatedRequestModel getAuditMessageWithTemplateIssuesRequestObject(
            Consumer<AuditMessageWithTemplateIssuesPaginatedRequestModel>... actions) {
        return getObjectWithData(
                AuditMessageWithTemplateIssuesPaginatedRequestModel
                        .builder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(50)
                        .build(),
                actions);
    }

    private PaginatedDeliveryFailedAuditMessageRequestModel getFailedAuditMessageRequestObject(
            Consumer<PaginatedDeliveryFailedAuditMessageRequestModel>... actions) {
        return getObjectWithData(
                PaginatedDeliveryFailedAuditMessageRequestModel
                        .builder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .pageSize(50)
                        .build(),
                actions);
    }

    public List<AuditMessageResponseModel> getAuditMessagesByPath(
            String apiPath,
            MessageType messageType,
            DeliveryStatus deliveryStatus,
            int currentPage,
            int pageSize,
            Consumer<PaginatedAuditMessageRequestModel>... actions) {

        Consumer<PaginatedAuditMessageRequestModel>[] standardActions = new Consumer[]{};
        val standardActionsStream = Stream
                .of((Consumer<PaginatedAuditMessageRequestModel>) (x -> x.setMessageType(messageType.getType())),
                        (Consumer<PaginatedAuditMessageRequestModel>) (x -> x
                                .setDeliveryStatus(deliveryStatus.getStatus())),
                        x -> x.setCurrentPage(currentPage),
                        x -> x.setPageSize(pageSize));
        val allActions = combineActions(standardActionsStream, actions);
        return getHeader(getAdminToken())
                .body(getAuditMessageRequestObject(
                        x -> x.setMessageType(messageType.getType()),
                        x -> x.setDeliveryStatus(deliveryStatus.getStatus()),
                        x -> x.setCurrentPage(currentPage),
                        x -> x.setPageSize(pageSize)
                ))
                .post(apiPath)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedAuditMessageResponseModel.class)
                .getAuditMessages();
    }

    public List<AuditMessageResponseModel> getAuditMessagesFromServiceByTypeAndStatus(
            MessageType messageType,
            DeliveryStatus deliveryStatus) {
        return getHeader(getAdminToken())
                .body(getAuditMessageRequestObject(
                        x -> x.setMessageType(messageType.getType()),
                        x -> x.setDeliveryStatus(deliveryStatus.getStatus())
                ))
                .post(MESSAGE_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedAuditMessageResponseModel.class)
                .getAuditMessages();
    }

    public List<DeliveryFailedAuditMessageResponseModel> getFailedDeliveryAuditMessagesByType(MessageType messageType) {
        return getHeader(getAdminToken())
                .body(getFailedAuditMessageRequestObject(
                        x -> x.setMessageType(messageType.getType())
                ))
                .post(FAILED_DELIVERY_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedDeliveryFailedAuditMessageResponseModel.class)
                .getAuditMessages();
    }
}
