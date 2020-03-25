package com.lykke.tests.api.base;

import static com.lykke.tests.api.common.ConfigUtils.getBackendUrl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PathConsts {

    private static final String ENVIRONMENT_URL = getBackendUrl();

    public static String getBaseUrl(ComponentBaseUrl componentBase) {
        return componentBase.getPath() + ENVIRONMENT_URL;
    }

    public static String getIsAlivePath(ComponentBaseUrl componentBase) {
        return getBaseUrl(componentBase) + CommonPaths.IS_ALIVE.getPath();
    }

    public static String getFullPath(String path, String param1, String param2) {
        return String.format(path, param1, param2);
    }

    public static String getFullPath(String path, String param) {
        return String.format(path, param);
    }

    @AllArgsConstructor
    public enum ComponentBaseUrl {
        PERSONAL_DATA_COMPONENT_URL("personal-data.services", ""),
        CUSTOMER_API_COMPONENT_URL("customer-api.services", buildNamespace("CustomerApi")),
        CUSTOMER_MANAGEMENT_COMPONENT_URL("customermanagement.services", buildNamespace("CustomerManagement")),
        BONUS_ENGINE_URL("bonusengine.services", buildNamespace("BonusEngine")),
        ADMIN_API_COMPONENT_URL("admin-api.services", buildNamespace("AdminApi")),
        ADMIN_MANAGEMENT_COMPONENT_URL("admin-management.services", buildNamespace("AdminManagement")),
        WALLET_API_COMPONENT_URL("wallets.services", ""),
        WALLET_MANAGEMENT_API_COMPONENT_URL("walletmanagement.services", buildNamespace("WalletManagement")),
        MVN_UBE_INTEGRATION_COMPONENT_URL("mavn-ube-integration.services", ""),
        MVN_PROPERTY_INTEGRATION_COMPONENT_URL("mavn-property-integration.services",
                buildNamespace("MAVNPropertyIntegration")),
        NOTIFICATION_SYSTEM_ADAPTER_URL("notification-system-adapter.services",
                buildNamespace("NotificationSystemAdapter")),
        NOTIFICATION_SYSTEM_COMPONENT_URL("notification-system.services", buildNamespace("NotificationSystem")),
        NOTIFICATION_SYSTEM_BROKER_URL("notification-system-broker.services",
                buildNamespace("NotificationSystemBroker")),
        NOTIFICATION_SYSTEM_AUDIT_URL("notification-system-audit.services", buildNamespace("NotificationSystemAudit")),
        CREDENTIALS_ADMIN_COMPONENT_URL("credentials-admin.services", ""),
        REFERRAL_API_COMPONENT_URL("referral.services", buildNamespace("Referral")),
        CUSTOMER_PROFILE_URL("customer-profile.services", buildNamespace("CustomerProfile")),
        OPERATIONS_HISTORY_URL("operationshistory.services", buildNamespace("OperationsHistory")),
        BONUS_CUSTOMER_PROFILE_URL("bonuscustomerprofile.services", buildNamespace("BonusCustomerProfile")),
        CREDENTIALS_URL("credentials.services", buildNamespace("Credentials")),
        MVN_INTEGRATION_URL("mvnintegration.services", buildNamespace("MVNIntegration")),
        CURRENCY_CONVERTOR_URL("currencyconvertor.services", buildNamespace("CurrencyConvertor")),
        CAMPAIGNS_URL("campaign.services", buildNamespace("Campaign")),
        TOKENS_STATISTICS_JOB_URL("tokens-statistics.jobs", "Lykke.Job.TokensStatistics"),
        PRIVATE_BLOCKCHAIN_FACADE_URL("privateblockchainfacade.services", buildNamespace("PrivateBlockchainFacade")),
        SCHEDULER_URL("scheduler.services", buildNamespace("Scheduler")),
        PUSH_NOTIFICATIONS("push-notifications.services", buildNamespace("PushNotifications")),
        AGENT_MANAGEMENT("agent-management.services", buildNamespace("AgentManagement")),
        DICTIONARIES_API_COMPONENT_URL("dictionaries.services", buildNamespace("Dictionaries")),
        QUORUM_EXPLORER_URL("quorumexplorer.services", buildNamespace("QuorumExplorer")),
        DASHBOARD_STATISTICS_URL("dashboardstatistics.services", buildNamespace("DashboardStatistics")),
        PARTNER_API_URL("partner-api.services", buildNamespace("PartnerApi")),
        PARTNERS_INTEGRATION_URL("partners-integration.services", buildNamespace("PartnersIntegration")),
        PARTNER_MANAGEMENT_API_URL("partnermanagement.services", buildNamespace("PartnerManagement")),
        PARTNERS_PAYMENTS_URL("partners-payments.services", buildNamespace("PartnersPayments")),
        ELIGIBILITY_ENGINE_SERVICES_URL("eligibilityengine.services", buildNamespace("EligibilityEngine")),
        TIERS("tiers.services", buildNamespace("Tiers")),
        SMS_PROVIDER_MOCK("sms-provider-mock.services", ""),
        CROSS_CHAIN_WALLET_LINKER_URL("crosschain-walletlinker.services", buildNamespace("CrossChainWalletLinker")),
        CROSS_CHAIN_TRANSFERS_URL("crosschaintransfers.services", buildNamespace("CrossChainTransfers")),
        REAL_ESTATE_BONUS_AGENT_URL("realestatebonusagent.services", buildNamespace("RealEstateBonusAgent")),
        ETHEREUM_BRIDGE_URL("ethereum-bridge.services", buildNamespace("EthereumBridge")),
        REPORTING_URL("reporting.services", buildNamespace("Reporting")),
        VOUCHERS_URL("vouchers.services", buildNamespace("Vouchers")),
        QUORUM_OPERATION_EXECUTOR_URL("quorum-operation-executor.services", buildNamespace("QuorumOperationExecutor"));

        @Getter
        private String path;

        @Getter
        private String namespace;

        private static String buildNamespace(String uniquePart) {
            return String.format("Lykke.Service.%s", uniquePart);
        }
    }

    @AllArgsConstructor
    public enum DashboardStatisticsEndpoint {
        IS_ALIVE_PATH("/api/isalive"),
        CUSTOMERS_PATH("/api/customers"),
        LEADS_PATH("/api/leads"),
        TOKENS_PATH("/api/tokens");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum DictionariesEndpoint {
        SALESFORCE_PATH("/api/salesforce"),
        COUNTRIES_OF_RESIDENCE_PATH("/countriesOfResidence"),
        COUNTRY_PHONE_CODES_PATH("/countryPhoneCodes"),
        BY_ID("/%s"),
        COMMON_INFORMATION_PATH("/api/commonInformation");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum TokenStatisticsJobEndpoint {
        GENERAL_PATH("/api/general"),
        TOKENS_BY_DATE_PATH("/total"),
        TOKENS_CURRENT_PATH("/total/current"),
        TOKENS_SNAPSHOT_PATH("/sync"),
        BY_DAYS("/bydays");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum CustomerApiEndpoint {
        CUSTOMER_PATH("/api/customers"),
        AUTH_PATH("/api/auth"),
        WALLETS_PATH("/api/wallets"),
        REFERRALS_PATH("/api/referrals"),
        HOTELS_PATH("/hotels"),
        REFERRALS_ALL_PATH("/all"),
        REGISTER("/register"),
        LOGIN("/login"),
        LOGOUT("/logout"),
        GET_CUSTOMER_WALLET("/customer"),
        TRANSFER_PATH("/transfer"),
        TRANSFERS_PATH("/transfers"),
        CHANGE_PASSWORD_PATH("/change-password"),
        GENERATE_RESET_PW_LINK_PATH("/generateresetpasswordlink"),
        RESET_PASSWORD_PATH("/reset-password"),
        EMAILS_PATH("/api/emails"),
        VERIFICATION("/verification"),
        PROPERTIES_PATH("/properties"),
        HISTORY_PATH("/api/history"),
        OPERATIONS_PATH("/operations"),
        LEAD_PATH("/lead"),
        LEADS_PATH("/leads"),
        AGENTS_PATH("/api/agents"),
        SPEND_RULES_PATH("/api/spendrules"),
        SPEND_RULES_SEARCH_BY_ID_PATH("/search"),
        LIST_COUNTRIES_OF_RESIDENCE_PATH("/api/lists/countriesOfResidence"),
        LIST_COUNTRY_PHONE_CODES_PATH("/api/lists/countryPhoneCodes"),
        GOOGLE_REGISTER_PATH("/google-register"),
        GOOGLE_LOGIN_PATH("/google-login"),
        VALIDATE_RESET_PASSWORD_PATH("/validate-reset-password-identifier"),
        PASSWORD_VALIDATION_RULES_PATH("/password-validation-rules"),
        EARN_RULES_PATH("/api/earnrules"),
        @Deprecated
        BY_ID("/%s"),
        SEARCH_PATH("/search"),
        STAKING_PATH("/staking"),
        VERIFY_EMAIL_PATH("/verify-email"),
        PARTNERS_PATH("/api/partners"),
        MESSAGES_PATH("/messages"),
        @Deprecated
        MESSAGES_BY_ID_PATH("/messages/%s"),
        PAYMENTS_PATH("/payments"),
        PAYMENTS_APPROVAL_PATH("/payments/approval"),
        PAYMENTS_REJECTION_PATH("/payments/rejection"),
        PAYMENTS_PENDING_PATH("/payments/pending"),
        PAYMENTS_SUCCEEDED_PATH("/payments/succeeded"),
        PAYMENTS_FAILED_PATH("/payments/failed"),
        TRANSFER_PAYMENT_PATH("/payment-transfer"),
        LINK_REQUEST_PATH("/linkRequest"),
        EXTERNAL_TRANSFER_PATH("/external-transfer"),
        NEXT_FEE_PATH("/nextFee"),
        PHONES_PATH("/api/phones"),
        PHONES_VERIFY_PATH("/api/phones/verify"),
        COMMON_INFORMATION("/api/commonInformation"),
        PUSH_NOTIFICATIONA_REGISTRATIONS_PATH("/api/pushNotifications/registrations"),
        PIN_PATH("/pin"),
        PIN_CHECK_PATH("/pin/check"),
        MOBILE_PATH("/api/mobile"),
        SETTINGS_PATH("/settings"),
        VOUCHERS_PATH("/api/vouchers"),
        BUY_PATH("/buy");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }

    }

    @AllArgsConstructor
    public enum CustomerManagementEndpoint {
        CUSTOMERMANAGEMENT_AUTH_PATH("/api/auth"),
        CUSTOMERMANAGEMENT_CUSTOMERS_PATH("/api/customers"),
        REGISTER("/register"),
        LOGIN("/login"),
        RESET_PASSWORD_PATH("/resetpassword"),
        CHANGE_PASSWORD_PATH("/change-password"),
        PASSWORD_RESET_PATH("/password-reset"),
        EMAILS_PATH("/api/emails"),
        VERIFICATION("/verification"),
        BLOCK_PATH("/block"),
        UNBLOCK_PATH("/unblock"),
        BLOCK_STATUS_PATH("/blockStatus/%s"),
        BLOCK_STATUS_LIST_PATH("/blockStatus/list"),
        PHONES_PATH("/api/phones"),
        GENERATE_VERIFICATION_PATH("/generate-verification");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum AdminApiService {
        ADMIN_PATH("/api/auth"),
        LOGIN("/login"),
        LOGOUT("/logout"),
        CAMPAIGNS("/api/earnrules"),
        CUSTOMERS("/api/customers"),
        SEARCH_PATH("/search"),
        WALLETS("/api/wallets"),
        BY_CUSTOMER_ID_PATH("/%s"),
        @Deprecated
        CUSTOMER("/api/customers/%s"),
        ADMINS("/api/Admins"),
        GENERATE_SUGGESTED_PASSWORD_PATH("/generateSuggestedPassword"),
        AUTOFILL_DATA_PATH("/autofillData"),
        CONDITIONS_PATH("/api/BonusTypes"),
        BY_CAMPAIGN_ID("/%s"),
        CUSTOMER_DETAILS("/details"),
        CUSTOMER_HISTORY("/history"),
        STATISTICS_PATH("/api/statistics"),
        CUSTOMERS_PATH("/customers"),
        TOKENS_PATH("/tokens"),
        EMAIL_VERIFICATION("/api/public/email-verification"),
        @Deprecated
        BLOCK_CUSTOMER("/api/Customers/block/%s"),
        @Deprecated
        UNBLOCK_CUSTOMER("/api/Customers/unblock/%s"),
        @Deprecated
        BLOCK_CUSTOMER_WALLET("/api/Customers/blockWallet/%s"),
        @Deprecated
        UNBLOCK_CUSTOMER_WALLET("/api/Customers/unblockWallet/%s"),
        BURN_RULES("/api/burnrules"),
        VOUCHERS_PATH("/vouchers"),
        IMAGE_PATH("/image"),
        @Deprecated
        BY_BURN_RULE_ID_PATH("/%s"),
        DASHBOARD_PATH("/api/dashboard"),
        LEADS_PATH("/leads"),
        CURRENT_TOKENS_PATH("/tokens-current"),
        BLOCKS_PATH("/api/blocks"),
        @Deprecated
        BY_NUMBER_PATH("/byNumber/%s"),
        @Deprecated
        BY_HASH_PATH("/byHash/%s"),
        TRANSACTIONS_PATH("/api/transactions"),
        @Deprecated
        BY_NUMBER_TRANSACTIONS_PATH("/byNumber/%s/transactions"),
        @Deprecated
        BY_HASH_TRANSACTIONS_PATH("/byHash/%s/transactions"),
        EVENTS_PATH("/api/events"),
        @Deprecated
        BY_NUMBER_EVENTS_PATH("/byNumber/%s/events"),
        @Deprecated
        BY_HASH_EVENTS_PATH("/byHash/%s/events"),
        @Deprecated
        TRANSACTION_EVENTS_PATH("/events"),
        @Deprecated
        TRANSACTION_HASH_PATH("/%s"),
        PAYMENTS_PATH("/api/payments"),
        UNPROCESSED_PAYMENT_PATH("/unprocessed"),
        @Deprecated
        ACCEPT_PAYMENT_PATH("/accepted/%s"),
        @Deprecated
        REJECTED_PAYMENT_PATH("/rejected/%s"),
        PARTNERS_PATH("/api/partners"),
        GENERATE_CLIENT_SECRET_PATH("/generateclientsecret"),
        GENERATE_CLIENT_ID_PATH("/generateclientid"),
        @Deprecated
        BY_ID_PATH("/%s"),
        @Deprecated
        CUSTOMER_BALANCE("/api/Customers/%s/balance"),
        @Deprecated
        WALLET_ADDRESS_PATH("/%s/walletAddress"),
        @Deprecated
        PUBLIC_WALLET_ADDRESS_BY_ID_PATH("/%s/publicWalletAddress"),
        PUBLIC_WALLET_ADDRESS_PATH("/publicWalletAddress"),
        SETTINGS_PATH("/api/settings"),
        GLOBAL_CURRENCY_RATE_PATH("/globalCurrencyRate"),
        AGENT_REQUIREMENTS_PATH("/agentRequirements"),
        OPERATION_FEES_PATH("/operationFees"),
        REPORTS_PATH("/api/reports");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum AdminManagementService {
        ADMIN_MANAGEMENT_PATH("/api/admins"),
        AUTH_PATH("/api/auth"),
        AUTO_FILL_VALUES_PATH("/autofillValues"),
        REGISTER("/register"),
        UPDATE_PATH("/update"),
        UPDATE_PERMISSIONS_PATH("/updatePermissions"),
        GET_PERMISSIONS_PATH("/getPermissions"),
        LOGIN("/login"),
        LOGOUT("logout"),
        GET_ADMIN_USERS("/getadminusers"),
        GET_BY_EMAIL("/getbyemail"),
        PAGINATED_PATH("/paginated");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum WalletManagementApiEndpoint {
        WALLET_MNGMT_PATH("/api/wallets"),
        INCREASE_BALANCE_PATH("/increase-balance"),
        DECREASE_BALANCE_PATH("/decrease-balance"),
        TRANSFER_BALANCE_PATH("/transfer-balance"),
        BLOCK_WALLET_PATH("/block"),
        UNBLOCK_WALLET_PATH("/unblock"),
        BLOCK_STATUS_PATH("/blockStatus/%s");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum MAVNUbeIntegrationService {
        MVN_UBE_INTEGRATION_PATH("/api/mavn"),
        INCREASE_UPOINT_BALANCE_PATH("/increaseUPointsBalance"),
        DECREASE_UPOINT_BALANCE_PATH("/decreaseUPointsBalance"),
        PAYMENT_TRANSACTION_PATH("/paymentTransaction"),
        UPOINTS_BALANCE_PATH("/uPointsBalance"),
        VENUE("/venue");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum ReferralService {
        REFERRAL_PATH("/api/referrals"),
        // TODO: Update GET and POST to use REFERRAL_PATH
        GET_REFERRAL_PATH("/api/referrals/"),
        POST_REFERRAL_PATH("/api/referrals"),
        PURCHASES_BY_CUSTOMER_ID_PATH("/purchases/%s"),
        FRIENDS_BY_CUSTOMER_ID_PATH("/friends/%s"),
        REFERRAL_LEADS_PATH("/api/referral-leads"),
        REFERRAL_LEADS_APPROVED_PATH("/approved"),
        REFERRAL_LEADS_APPROVE_PATH("/approve"),
        REFERRAL_LEADS_PROPERTY_PURCHASE_PATH("/property-purchases"),
        REFERRAL_LEADS_STATISTIC_PATH("/statistic"),
        REFERRAL_HOTELS_PATH("/api/referral-hotels"),
        CONFIRM_PATH("/confirm"),
        USE_PATH("/use"),
        BY_REFERRER_ID_PATH("/byReferrerId"),
        BY_EMAIL_PATH("/byEmail"),
        GET_BY_EMAIL_PATH("/getbyemail"),
        COMMON_REFERRAL_PATH("/api/common-referral"),
        BY_CUSTOMER("/byCustomer"),
        LIST_PATH("/list");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum NotificationSystemAdapterService {
        KEYS_PATH("/api/keys/%s");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum CredentialsAdminService {
        CREDENTIALS_ADMIN_PATH("/api/credentials"),
        VALIDATE_CUSTOMER_CREDENTIALS_PATH("/validate"),
        REMOVE_CUSTOMER_CREDENTIALS_PATH("/%s");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum NotificationSystemService {
        TEMPLATES_PATH("/api/templates"),
        TEMPLATES_BY_NAME_PATH("/%s"),
        TEMPLATE_NAME_BY_LANGUAGE("/%s/%s"),
        NOTIFICATION_MESSAGE_PATH("/api/message"),
        EMAIL_PATH("/email"),
        SMS_PATH("/sms"),
        PUSH_NOTIFICATIONS_PATH("/pushNotification");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum NotificationSystemBrokerService {
        EMAIL_MESSAGE_PATH("/api/emailMessages");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum CustomerProfileApiEndpoint {
        ENCRYPTION_KEY_PATH("/api/encryptionKey"),
        CUSTOMER_PROFILE_PATH("/api/customers"),
        CUSTOMER_PROFILE_BY_ID_PATH("/%s"),
        PAGINATED_PATH("/paginated"),
        CUSTOMERS_IDS("/list"),
        SET_EMAIL_AS_VERIFIED_PATH("/setemailasverified/%s"),
        STATISTICS_PATH("/api/statistics"),
        BY_PHONE("/phone/%s"),
        PARTNER_CONTACTS_PATH("/api/partnerContacts"),
        BY_LOCATION("/%s"),
        PHONES_PATH("/api/phones"),
        PHONES_VERIFY_PATH("/api/phones/verify"),
        ADMIN_PROFILES_PATH("/api/adminProfiles"),
        ADMIN_ID_PATH("/%s");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum MVNIntegrationApiEndpoint {
        PURCHASES_PATH("/api/purchases");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum OperationsHistoryApiEndpoint {
        TRANSACTIONS_PATH("/api/transactions"),
        TRANSACTIONS_BY_ID_PATH("/%s"),
        TRANSACTIONS_BY_DATE("/from/%s/to/%s"),
        TRANSFERS_PATH("/api/transfers"),
        TRANSFERS_BY_ID_PATH("/%s"),
        ACTIVE_CUSTOMERS_PATH("/customers/active"),
        STATISTICS_PATH("/api/statistics"),
        TOKENS_PATH("/tokens"),
        CUSTOMERS_BY_DATE_PATH("/customers"),
        VOUCHER_PURCHASES_PATH("/api/voucher-purchases");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum BonusCustomerProfileApiEndpoint {
        CONTRIBUTIONS("/api/campaigns/contributions"),
        AGGREGATIONS("/api/customers"),
        CUSTOMER_BY_ID_PATH("/%s");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum CurrencyConvertorApiEndpoint {
        CURRENCIES_PARAM_PATH("/%s"),
        CONVERTER_PATH("/api/converter"),
        CURRENCY_RATES_PATH("/api/currencyRates"),
        GLOBAL_CURRENCY_RATES_PATH("/api/globalCurrencyRates");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum CredentialsEndpoint {
        CREDENTIALS_PATH("/api/credentials"),
        RESET_IDENTIFIER_PATH("/resetIdentifier/%s"),
        PASSWORD_RESET_PATH("/password-reset"),
        PARTNERS_PATH("/api/partners"),
        PARTNERS_VALIDATE_PATH("/api/partners/validate"),
        CLIENT_ID_PATH("/clientId"),
        CLIENT_SECRET_PATH("/clientSecret"),
        PIN_PATH("/pin"),
        VALIDATE_PIN_PATH("/pin/validate"),
        HAS_PIN_PATH("/pin/has-pin");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum NotificationSystemAuditEndpoint {
        MESSAGES_ALL("/api/messages/all"),
        MESSAGES_TEMPLATE_PARSING_ISSUES("/api/messages/templateParsingIssues"),
        MESSAGES_FAILED_DELIVERY("/api/messages/failedDelivery");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum CommonPaths {
        IS_ALIVE("/api/IsAlive");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum CampaignsEndpoint {
        CAMPAIGNS_PATH("/api/campaigns"),
        CAMPAIGN_BY_ID_PATH("/%s"),
        ACTIVE_CAMPAIGN_PATH("/active"),
        BONUS_TYPES_PATH("/api/bonusTypes"),
        BY_TYPE("/%s"),
        ACTIVE_TYPES_PATH("/active"),
        CAMPAIGN_ALL_PATH("/all"),
        BURN_RULES_PATH("/api/burn-rules"),
        IMAGE_PATH("/image"),
        BURN_RULE_ID("/%s"),
        MOBILE_BURN_RULES_PATH("/api/mobile/burn-rules"),
        MOBILE_EARN_RULES_PATH("/api/mobile/earn-rules"),
        BY_ID("/%s"),
        HISTORY_PATH("/api/history");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }

    }

    @AllArgsConstructor
    public enum PrivateBlockchainFacadeEndpoint {
        BONUSES("/api/bonuses"),
        CUSTOMER_BALANCE("/api/customers/%s/balance"),
        OPERATIONS_NEW("/api/operations/new"),
        OPERATIONS_ACCEPTED("/api/operations/accepted"),
        OPERATIONS_FAILED("/api/operations/%s/failed"),
        OPERATIONS_SUCCEEDED("/api/operations/%s/succeeded"),
        TOTAL_AMOUNT("/api/tokens/total-supply"),
        TRANSFERS("/api/transfers"),
        WALLETS("/api/wallets"),
        GENERIC_TRANSFERS_PATH("/api/generic-transfers");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum MAVNPropertyIntegrationEndpoint {
        LEADS_PATH("/api/leads"),
        REGISTERED_LEADS_PATH("/api/history/registeredLeads"),
        APPROVED_LEADS_PATH("/api/history/approvedLeads"),
        PROPERTY_PURCHASES_PATH("/api/history/propertyPurchasesByLeads"),
        REGISTERED_LEADS_MANUAL_PATH("/api/manualEntry/registeredLeads"),
        APPROVED_LEADS_MANUAL_PATH("/api/manualEntry/approvedLeads"),
        PROPERTY_PURCHASES_MANUAL_PATH("/api/manualEntry/propertyPurchasesByLeads"),
        LEADS_CHANGED_SALESMEN_MANUAL_PATH("/api/manualEntry/leadsChangedSalesmen"),
        REGISTERED_AGENTS_PATH("/api/history/registeredAgents"),
        PROCESSED_AGENTS_PATH("/api/history/processedAgents"),
        REGISTERED_AGENTS_MANUAL_PATH("/api/manualEntry/registeredAgents"),
        PROCESSED_AGENTS_MANUAL_PATH("/api/manualEntry/processedAgents"),
        AGENTS_CHANGED_SALESMEN_MANUAL_PATH("/api/manualEntry/agentsChangedSalesmen"),
        SALESMAN_SALESFORCE_ID("/api/salesmen/%s"),
        AGENTS_CHANGED_SALESMEN_PATH("/api/history/agentsChangedSalesmen"),
        LEADS_CHANGED_SALESMEN_PATH("/api/history/leadsChangedSalesmen"),
        PAID_INVOICES_MANUAL_PATH("/api/manualEntry/paidInvoices"),
        PAID_INVOICES_HISTORY_PATH("/api/history/paidInvoices"),
        PAID_INVOICES_INTEGRATION_PATH("/api/paidInvoices"),
        PENDING_INVOICE_PAYMENTS_PATH("/api/pendingInvoicePayments/%s"),
        PAID_INVOICES_PAYMENTS_PATH("/api/history/paidInvoicePayments"),
        PAID_INVOICES_PAYMENTS_MANUAL_PATH("/api/manualEntry/paidInvoicePayments"),
        AGENTS_UPLOAD_IMAGES_PATH("/api/agents/uploadImages");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum BonusEngineEndpoint {
        CAMPAIGNS_PATH("/api/campaigns"),
        SIMULATE_TRIGGER("/simulate-trigger"),
        SIMULATE_EVENT_CHANGE_PATH("/simulate-event-change"),
        UPDATE_ACTIVE_CAMPAIGNS_PATH("/update-active-campaigns"),
        CAMPAIGN_COMPLETION_PATH("/api/customers/campaign-completion/%s/%s");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum PushNotificationsEndpoint {
        REGISTRATIONS("/api/pushRegistrations"),
        CUSTOMER_ID("/api/pushRegistrations/queryCustomer/%s"),
        REGISTRATION_ID("/api/pushRegistrations/%s"),
        INFOBIP_TOKEN("/api/pushRegistrations/infobip/%s"),
        NOTIFICATION_MESSAGES_PATH("/api/notificationMessages"),
        READ_PATH("/read"),
        READ_ALL_PATH("/read/all"),
        UNREAD_COUNT_PATH("/unread/count");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum AgentManagementApiEndpoint {
        AGENTS_PATH("/api/agents"),
        REQUIREMENTS_PATH("/api/requirements"),
        REQUIREMENTS_TOKENS_PATH("/api/requirements/tokens"),
        LIST_PATH("/list");

        @Getter
        private String path;
    }

    @AllArgsConstructor
    public enum QuorumExplorerEndpoint {
        BLOCKS_PATH("/api/blocks"),
        BY_NUMBER_PATH("/byNumber/%s"),
        BY_HASH_PATH("/byHash/%s"),
        TRANSACTIONS_PATH("/api/transactions"),
        BY_NUMBER_TRANSACTIONS_PATH("/byNumber/transactions"),
        BY_HASH_TRANSACTIONS_PATH("/byHash/transactions"),
        EVENTS_PATH("/api/events"),
        BY_NUMBER_EVENTS_PATH("/byNumber/events"),
        BY_HASH_EVENTS_PATH("/byHash/events"),
        TRANSACTION_EVENTS_PATH("/events"),
        TRANSACTION_HASH_PATH("/%s");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum PartnerApiEndpoint {
        LOGIN_PATH("/api/auth/login"),
        LOGOUT_PATH("/api/auth/logout"),
        CUSTOMERS_PATH("/api/customers"),
        @Deprecated
        CUSTOMER_BALANCE_PATH("/api/balance/%s"),
        QUERY_PATH("/api/balance/query"),
        REFERRALS_PATH("/api/referrals"),
        BONUS_CUSTOMERS_PATH("/api/bonus/customers"),
        PAYMENTS_PATH("/api/payments"),
        REQUESTS_PATH("/requests"),
        MESSAGES_PATH("/api/messages");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum PartnersIntegrationEndpoint {
        CUSTOMERS_QUERY_PATH("/api/customers/query"),
        CUSTOMER_BALANCE_QUERY_PATH("/api/customers/balance/%s"),
        REFERRALS_QUERY_PATH("/api/referrals/query"),
        BONUS_CUSTOMERS_PATH("/api/bonus/customers"),
        PAYMENTS_PATH("/api/payments"),
        REQUESTS_PATH("/requests"),
        MESSAGES_PATH("/api/messages"),
        MESSAGE_PATH("/api/messages/%s");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum PartnerManagementEndpoint {
        LOGIN_PATH("/api/auth/login"),
        GENERATE_CLIENT_ID_PATH("/api/auth/generateClientId"),
        GENERATE_CLIENT_SECRET_PATH("/api/auth/generateClientSecret"),
        PARTNERS_PATH("/api/partners"),
        BY_ID_PATH("/%s"),
        BY_CLIENT_ID_PATH("/byClientId/%s"),
        BY_LOCATION_ID_PATH("/byLocationId/%s"),
        LIST_PATH("/list");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum PartnersPaymetsEndpoint {
        PAYMENTS_PATH("/api/payments"),
        CUSTOMER_PENDING_PATH("/customer/pending"),
        CUSTOMER_SUCCEEDED_PATH("/customer/succeeded"),
        CUSTOMER_FAILED_PATH("/customer/failed"),
        CUSTOMER_APPROVAL_PATH("/customer/approval"),
        CUSTOMER_REJECTION_PATH("/customer/rejection"),
        PARTNER_APPROVAL_PATH("/partner/approval"),
        PARTNER_REJECTION_PATH("/partner/cancellation"),
        PAYMENT_REQUEST_ID_PATH("/%s");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum EligibilityServicesEndpoint {
        CONVERSION_RATE_PATH("/api/conversionRate"),
        PARTNER_RATE_PATH("/partnerRate"),
        EARN_RULE_RATE_PATH("/earnRuleRate"),
        SPEND_RULE_RATE_PATH("/spendRuleRate"),
        PARTNER_AMOUNT_PATH("/partnerAmount"),
        EARN_RULE_AMOUNT_PATH("/earnRuleAmount"),
        SPEND_RULE_AMOUNT_PATH("/spendRuleAmount"),
        CONDITION_AMOUNT_PATH("/conditionAmount"),
        CONDITION_RATE_PATH("/conditionRate");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum TiersEndpoint {
        CUSTOMERS_BY_ID_TIER_PATH("/api/customers/%s/tier"),
        REPORTS_NUMBER_OF_CUSTOMERS_PER_TIER_PATH("/api/reports/numberOfCustomersPerTier"),
        TIERS_PATH("/api/tiers"),
        BY_ID_PATH("/%s");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum SmsProviderMockEndpoint {
        SMS_PATH("/api/sms"),
        SMS_QUERY_PATH("/query"),
        BY_ID_PATH("/%s"),
        SENT_SMS_PATH("/sentsms"),
        SMS_DETAILS_PATH("/details");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum CrossChainWalletLinkerEndpoint {
        CUSTOMERS_PATH("/api/customers"),
        CUSTOMER_PUBLIC_ADDRESS_PATH("/%s/publicAddress"),
        CUSTOMER_NEXT_FEE_PATH("/%s/nextFee"),
        LINK_REQUESTS_PATH("/api/linkRequests"),
        LINK_REQUEST_APPROVAL_PATH("/approval"),
        CONFIGURATION_PATH("/api/configuration"),
        CONFIGURATION_TYPE_PATH("/%s");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum CrossChainTransfersEndpoint {
        CROSS_CHAIN_TRANSFERS_PATH("/api/cross-chain-transfers"),
        TO_EXTERNAL_PATH("/to-external");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum EthereumBridgeEndpoint {
        WALLETS_PATH("/api/wallets"),
        BY_WALLET_ADDRESS_PATH("/%s/balance");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum ReportingEndpoint {
        REPORT_PATH("/api/report"),
        CSV_PATH("/csv");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum VouchersEndpoint {
        REPORTS_PATH("/api/reports"),
        SPEND_RULE_VOUCHERS_PATH("/spendRuleVouchers"),
        VOUCHERS_PATH("/api/vouchers"),
        BY_VOUCHER_ID_PATH("/%s"),
        GET_VOUCHERS_BY_SPEND_RULE_ID_PATH("/api/spendRules/%s/vouchers"),
        GET_CUSTOMERS_VOUCHERS_PATH("/api/customers/%s/vouchers"),
        CUSTOMERS_PATH("/api/customers");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }

    @AllArgsConstructor
    public enum QuorumOperationExecutorEndpoint {
        ADDRESSES_PATH("/api/addresses"),
        BALANCE_BY_ADDRESS_PATH("/%s/balance");

        @Getter
        private String path;

        public String getFilledInPath(String param) {
            return String.format(path, param);
        }
    }
}
