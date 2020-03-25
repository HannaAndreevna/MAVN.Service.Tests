package com.lykke.tests.api.common.enums;

public enum Service {
    ADMIN_API("Lykke.Service.AdminApi"),
    ADMIN_MANAGEMENT("Lykke.Service.AdminManagement"),
    BONUS_ENGINE("Lykke.Service.BonusEngine"),
    CREDENTIALS("Lykke.Service.Credentials"),
    CURRENCY_CONVERTOR("Lykke.Service.CurrencyConvertor"),
    CUSTOMER_API("Lykke.Service.CustomerApi"),
    CUSTOMER_MANAGEMENT("Lykke.Service.CustomerManagement"),
    CUSTOMER_PROFILE("Lykke.Service.CustomerProfile"),
    MVN_UBE_INTEGRATION("Lykke.Service.MAVNUbeIntegration"),
    NOTIFICATION_SYSTEM("Lykke.Service.NotificationSystem"),
    NOTIFICATION_SYSTEM_ADAPTER("Lykke.Service.NotificationSystemAdapter"),
    NOTIFICATION_SYSTEM_AUDIT("Lykke.Service.NotificationSystemAudit"),
    NOTIFICATION_SYSTEM_BROKER("Lykke.Service.NotificationSystemBroker"),
    OPERATION_HISTORY("Lykke.Service.OperationsHistory"),
    REFERRAL("Lykke.Service.Referral"),
    WALLETS("Lykke.Service.Wallets"),
    WALLET_MANAGEMENT("Lykke.Service.WalletManagement"),
    CAMPAIGN("Lykke.Service.Campaign"),
    SCHEDULER("Lykke.Service.Scheduler"),
    DASHBOARD_STATISTICS("Lykke.Service.DashboardStatistics");

    private String namespace;

    public String getNamespace() {
        return this.namespace;
    }

    Service(String namespace) {
        this.namespace = namespace;
    }

}
