package com.lykke.tests.api.common;

import com.lykke.api.testing.api.common.model.rabbitmq.RabbitMqData;

public class RabbitMqTemporaryConsts {

    public static class MAVNPropertyIntegration {

        public static final String RABBITMQ_HOST = "rabbit-main.rabbit.svc.cluster.local";
        public static final String RABBITMQ_REGISTER_EXCHANGE = "lykke.mavn.propertyintegration.agentregisterrequest";
        public static final String RABBITMQ_REGISTER_RESPONSE_EXCHANGE =
                "lykke.mavn.propertyintegration.agentregisterresponse";
        public static final String RABBITMQ_QUEUE_SUFFIX = "mavnpropertyintegration";
        public static final String RABBITMQ_USERNAME = "mavn.history";
        public static final String RABBITMQ_PASSWORD = "mavn.history";

        public static RabbitMqData getRabbitMqRequestData() {
            return RabbitMqData
                    .builder()
                    .host(RABBITMQ_HOST)
                    .exchange(RABBITMQ_REGISTER_EXCHANGE)
                    .username(RABBITMQ_USERNAME)
                    .password(RABBITMQ_PASSWORD)
                    .build();
        }

        public static RabbitMqData getRabbitMqResponseData() {
            return RabbitMqData
                    .builder()
                    .host(RABBITMQ_HOST)
                    .exchange(RABBITMQ_REGISTER_RESPONSE_EXCHANGE)
                    .queue(RABBITMQ_QUEUE_SUFFIX)
                    .username(RABBITMQ_USERNAME)
                    .password(RABBITMQ_PASSWORD)
                    .build();
        }
    }

    public static class NotificationSystem {

        public static final String SMS_EXCHANGE = "lykke.notificationsystem.command.sms";
        public static final String PUBLISH_NOTIFICATION_EXCHANGE = "lykke.notificationsystem.command.pushnotification";
        public static final String EMAIL_MESSAGE_EXCHANGE = "lykke.notificationsystem.command.emailmessage";
    }

    public static class NotificatioonSystemAudit {

        public static final String RABBITMQ_HOST = "rabbit-main.rabbit.svc.cluster.local";
        public static final String RABBITMQ_CREATE_MESSAGE_EXCHANGE = "lykke.notificationsystem.createauditmessage";
        public static final String RABBITMQ_UPDATE_MESSAGE_EXCHANGE = "lykke.notificationsystem.updateauditmessage";
        public static final String RABBITMQ_REGISTER_RESPONSE_EXCHANGE =
                "lykke.mavn.propertyintegration.agentregisterresponse";
        public static final String RABBITMQ_QUEUE_SUFFIX = "notificationsystemaudit";
        public static final String RABBITMQ_USERNAME = "mavn.history";
        public static final String RABBITMQ_PASSWORD = "mavn.history";
    }

    public static class NotificatioonSystemBroker {

        public static final String RABBITMQ_HOST = "rabbit-main.rabbit.svc.cluster.local";
        public static final String RABBITMQ_EXCHANGE = "lykke.notificationsystem.brokermessage";
        public static final String RABBITMQ_UPDATE_MESSAGE_EXCHANGE = "lykke.notificationsystem.updateauditmessage";
        public static final String RABBITMQ_QUEUE_SUFFIX = "notificationsystembroker";
        public static final String RABBITMQ_USERNAME = "mavn.history";
        public static final String RABBITMQ_PASSWORD = "mavn.history";
    }

    public static class PrivateBlockchainFacade {

        public static final String TRANSACTION_FAILED_EXCHANGE = "lykke.wallet.transactionfailed";
    }
}
