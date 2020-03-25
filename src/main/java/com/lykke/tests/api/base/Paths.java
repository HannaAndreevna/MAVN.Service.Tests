package com.lykke.tests.api.base;

import static com.lykke.tests.api.base.PathConsts.AdminApiService.ACCEPT_PAYMENT_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.ADMINS;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.ADMIN_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.BLOCK_CUSTOMER;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.BLOCK_CUSTOMER_WALLET;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.BY_CAMPAIGN_ID;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.CAMPAIGNS;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.CONDITIONS_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.CUSTOMER;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.CUSTOMERS;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.CUSTOMER_HISTORY;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.DASHBOARD_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.EMAIL_VERIFICATION;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.GENERATE_CLIENT_ID_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.GENERATE_CLIENT_SECRET_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.IMAGE_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.REPORTS_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.SEARCH_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.UNBLOCK_CUSTOMER;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.UNBLOCK_CUSTOMER_WALLET;
import static com.lykke.tests.api.base.PathConsts.AdminApiService.UNPROCESSED_PAYMENT_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminManagementService.ADMIN_MANAGEMENT_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminManagementService.AUTO_FILL_VALUES_PATH;
import static com.lykke.tests.api.base.PathConsts.AdminManagementService.GET_ADMIN_USERS;
import static com.lykke.tests.api.base.PathConsts.AdminManagementService.REGISTER;
import static com.lykke.tests.api.base.PathConsts.AgentManagementApiEndpoint.REQUIREMENTS_PATH;
import static com.lykke.tests.api.base.PathConsts.AgentManagementApiEndpoint.REQUIREMENTS_TOKENS_PATH;
import static com.lykke.tests.api.base.PathConsts.BonusCustomerProfileApiEndpoint.AGGREGATIONS;
import static com.lykke.tests.api.base.PathConsts.BonusCustomerProfileApiEndpoint.CONTRIBUTIONS;
import static com.lykke.tests.api.base.PathConsts.BonusCustomerProfileApiEndpoint.CUSTOMER_BY_ID_PATH;
import static com.lykke.tests.api.base.PathConsts.BonusEngineEndpoint.SIMULATE_TRIGGER;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.BONUS_TYPES_PATH;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.BURN_RULES_PATH;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.BURN_RULE_ID;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.CAMPAIGNS_PATH;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.MOBILE_BURN_RULES_PATH;
import static com.lykke.tests.api.base.PathConsts.CampaignsEndpoint.MOBILE_EARN_RULES_PATH;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.ADMIN_API_COMPONENT_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.ADMIN_MANAGEMENT_COMPONENT_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.AGENT_MANAGEMENT;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.BONUS_CUSTOMER_PROFILE_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.BONUS_ENGINE_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.CAMPAIGNS_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.CREDENTIALS_ADMIN_COMPONENT_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.CREDENTIALS_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.CROSS_CHAIN_TRANSFERS_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.CROSS_CHAIN_WALLET_LINKER_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.CURRENCY_CONVERTOR_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.CUSTOMER_API_COMPONENT_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.CUSTOMER_MANAGEMENT_COMPONENT_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.CUSTOMER_PROFILE_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.DASHBOARD_STATISTICS_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.DICTIONARIES_API_COMPONENT_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.ELIGIBILITY_ENGINE_SERVICES_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.MVN_PROPERTY_INTEGRATION_COMPONENT_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.MVN_UBE_INTEGRATION_COMPONENT_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.ETHEREUM_BRIDGE_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.MVN_INTEGRATION_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.NOTIFICATION_SYSTEM_ADAPTER_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.NOTIFICATION_SYSTEM_AUDIT_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.NOTIFICATION_SYSTEM_BROKER_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.NOTIFICATION_SYSTEM_COMPONENT_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.OPERATIONS_HISTORY_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.PARTNERS_INTEGRATION_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.PARTNERS_PAYMENTS_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.PARTNER_API_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.PARTNER_MANAGEMENT_API_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.PRIVATE_BLOCKCHAIN_FACADE_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.PUSH_NOTIFICATIONS;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.QUORUM_EXPLORER_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.QUORUM_OPERATION_EXECUTOR_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.REFERRAL_API_COMPONENT_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.REPORTING_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.SMS_PROVIDER_MOCK;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.TIERS;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.TOKENS_STATISTICS_JOB_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.VOUCHERS_URL;
import static com.lykke.tests.api.base.PathConsts.ComponentBaseUrl.WALLET_MANAGEMENT_API_COMPONENT_URL;
import static com.lykke.tests.api.base.PathConsts.CredentialsAdminService.CREDENTIALS_ADMIN_PATH;
import static com.lykke.tests.api.base.PathConsts.CredentialsEndpoint.CREDENTIALS_PATH;
import static com.lykke.tests.api.base.PathConsts.CredentialsEndpoint.PARTNERS_PATH;
import static com.lykke.tests.api.base.PathConsts.CredentialsEndpoint.PARTNERS_VALIDATE_PATH;
import static com.lykke.tests.api.base.PathConsts.CurrencyConvertorApiEndpoint.CURRENCY_RATES_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.AUTH_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.CHANGE_PASSWORD_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.CUSTOMER_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.COMMON_INFORMATION;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.EARN_RULES_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.EXTERNAL_TRANSFER_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.GET_CUSTOMER_WALLET;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.GOOGLE_LOGIN_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.GOOGLE_REGISTER_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.HISTORY_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.LINK_REQUEST_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.LIST_COUNTRIES_OF_RESIDENCE_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.LIST_COUNTRY_PHONE_CODES_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.NEXT_FEE_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.OPERATIONS_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.PASSWORD_VALIDATION_RULES_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.PAYMENTS_APPROVAL_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.PAYMENTS_FAILED_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.PAYMENTS_PENDING_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.PAYMENTS_REJECTION_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.PAYMENTS_SUCCEEDED_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.REFERRALS_ALL_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.REFERRALS_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.RESET_PASSWORD_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.SPEND_RULES_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.TRANSFER_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.TRANSFER_PAYMENT_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.VALIDATE_RESET_PASSWORD_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.VERIFICATION;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.VERIFY_EMAIL_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.WALLETS_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerManagementEndpoint.CUSTOMERMANAGEMENT_AUTH_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerManagementEndpoint.CUSTOMERMANAGEMENT_CUSTOMERS_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerProfileApiEndpoint.BY_LOCATION;
import static com.lykke.tests.api.base.PathConsts.CustomerProfileApiEndpoint.BY_PHONE;
import static com.lykke.tests.api.base.PathConsts.CustomerProfileApiEndpoint.CUSTOMERS_IDS;
import static com.lykke.tests.api.base.PathConsts.CustomerProfileApiEndpoint.CUSTOMER_PROFILE_BY_ID_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerProfileApiEndpoint.CUSTOMER_PROFILE_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerProfileApiEndpoint.ENCRYPTION_KEY_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerProfileApiEndpoint.PAGINATED_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerProfileApiEndpoint.PARTNER_CONTACTS_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerProfileApiEndpoint.SET_EMAIL_AS_VERIFIED_PATH;
import static com.lykke.tests.api.base.PathConsts.DashboardStatisticsEndpoint.IS_ALIVE_PATH;
import static com.lykke.tests.api.base.PathConsts.DashboardStatisticsEndpoint.TOKENS_PATH;
import static com.lykke.tests.api.base.PathConsts.DictionariesEndpoint.BY_ID;
import static com.lykke.tests.api.base.PathConsts.DictionariesEndpoint.COUNTRIES_OF_RESIDENCE_PATH;
import static com.lykke.tests.api.base.PathConsts.DictionariesEndpoint.COUNTRY_PHONE_CODES_PATH;
import static com.lykke.tests.api.base.PathConsts.DictionariesEndpoint.SALESFORCE_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.AGENTS_CHANGED_SALESMEN_MANUAL_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.AGENTS_CHANGED_SALESMEN_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.AGENTS_UPLOAD_IMAGES_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.APPROVED_LEADS_MANUAL_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.APPROVED_LEADS_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.LEADS_CHANGED_SALESMEN_MANUAL_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.LEADS_CHANGED_SALESMEN_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.LEADS_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.PAID_INVOICES_HISTORY_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.PAID_INVOICES_INTEGRATION_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.PAID_INVOICES_MANUAL_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.PAID_INVOICES_PAYMENTS_MANUAL_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.PAID_INVOICES_PAYMENTS_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.PENDING_INVOICE_PAYMENTS_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.PROCESSED_AGENTS_MANUAL_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.PROCESSED_AGENTS_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.PROPERTY_PURCHASES_MANUAL_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.PROPERTY_PURCHASES_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.REGISTERED_AGENTS_MANUAL_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.REGISTERED_AGENTS_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.REGISTERED_LEADS_MANUAL_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.REGISTERED_LEADS_PATH;
import static com.lykke.tests.api.base.PathConsts.MAVNPropertyIntegrationEndpoint.SALESMAN_SALESFORCE_ID;
import static com.lykke.tests.api.base.PathConsts.MAVNUbeIntegrationService.MVN_UBE_INTEGRATION_PATH;
import static com.lykke.tests.api.base.PathConsts.MVNIntegrationApiEndpoint.PURCHASES_PATH;
import static com.lykke.tests.api.base.PathConsts.NotificationSystemAuditEndpoint.MESSAGES_ALL;
import static com.lykke.tests.api.base.PathConsts.NotificationSystemAuditEndpoint.MESSAGES_FAILED_DELIVERY;
import static com.lykke.tests.api.base.PathConsts.NotificationSystemAuditEndpoint.MESSAGES_TEMPLATE_PARSING_ISSUES;
import static com.lykke.tests.api.base.PathConsts.NotificationSystemBrokerService.EMAIL_MESSAGE_PATH;
import static com.lykke.tests.api.base.PathConsts.NotificationSystemService.EMAIL_PATH;
import static com.lykke.tests.api.base.PathConsts.NotificationSystemService.NOTIFICATION_MESSAGE_PATH;
import static com.lykke.tests.api.base.PathConsts.NotificationSystemService.PUSH_NOTIFICATIONS_PATH;
import static com.lykke.tests.api.base.PathConsts.NotificationSystemService.SMS_PATH;
import static com.lykke.tests.api.base.PathConsts.NotificationSystemService.TEMPLATES_PATH;
import static com.lykke.tests.api.base.PathConsts.OperationsHistoryApiEndpoint.ACTIVE_CUSTOMERS_PATH;
import static com.lykke.tests.api.base.PathConsts.OperationsHistoryApiEndpoint.CUSTOMERS_BY_DATE_PATH;
import static com.lykke.tests.api.base.PathConsts.OperationsHistoryApiEndpoint.TRANSACTIONS_BY_ID_PATH;
import static com.lykke.tests.api.base.PathConsts.OperationsHistoryApiEndpoint.TRANSACTIONS_PATH;
import static com.lykke.tests.api.base.PathConsts.OperationsHistoryApiEndpoint.TRANSFERS_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnerApiEndpoint.CUSTOMER_BALANCE_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnerApiEndpoint.LOGIN_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnerApiEndpoint.LOGOUT_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnerApiEndpoint.MESSAGES_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnersIntegrationEndpoint.CUSTOMERS_QUERY_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnersIntegrationEndpoint.CUSTOMER_BALANCE_QUERY_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnersIntegrationEndpoint.REFERRALS_QUERY_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnersPaymetsEndpoint.CUSTOMER_APPROVAL_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnersPaymetsEndpoint.CUSTOMER_FAILED_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnersPaymetsEndpoint.CUSTOMER_PENDING_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnersPaymetsEndpoint.CUSTOMER_REJECTION_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnersPaymetsEndpoint.CUSTOMER_SUCCEEDED_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnersPaymetsEndpoint.PARTNER_APPROVAL_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnersPaymetsEndpoint.PARTNER_REJECTION_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnersPaymetsEndpoint.PAYMENTS_PATH;
import static com.lykke.tests.api.base.PathConsts.PartnersPaymetsEndpoint.PAYMENT_REQUEST_ID_PATH;
import static com.lykke.tests.api.base.PathConsts.PrivateBlockchainFacadeEndpoint.BONUSES;
import static com.lykke.tests.api.base.PathConsts.PrivateBlockchainFacadeEndpoint.CUSTOMER_BALANCE;
import static com.lykke.tests.api.base.PathConsts.PrivateBlockchainFacadeEndpoint.GENERIC_TRANSFERS_PATH;
import static com.lykke.tests.api.base.PathConsts.PrivateBlockchainFacadeEndpoint.OPERATIONS_ACCEPTED;
import static com.lykke.tests.api.base.PathConsts.PrivateBlockchainFacadeEndpoint.OPERATIONS_FAILED;
import static com.lykke.tests.api.base.PathConsts.PrivateBlockchainFacadeEndpoint.OPERATIONS_NEW;
import static com.lykke.tests.api.base.PathConsts.PrivateBlockchainFacadeEndpoint.OPERATIONS_SUCCEEDED;
import static com.lykke.tests.api.base.PathConsts.PrivateBlockchainFacadeEndpoint.TOTAL_AMOUNT;
import static com.lykke.tests.api.base.PathConsts.PrivateBlockchainFacadeEndpoint.TRANSFERS;
import static com.lykke.tests.api.base.PathConsts.PushNotificationsEndpoint.CUSTOMER_ID;
import static com.lykke.tests.api.base.PathConsts.PushNotificationsEndpoint.INFOBIP_TOKEN;
import static com.lykke.tests.api.base.PathConsts.PushNotificationsEndpoint.REGISTRATIONS;
import static com.lykke.tests.api.base.PathConsts.PushNotificationsEndpoint.REGISTRATION_ID;
import static com.lykke.tests.api.base.PathConsts.QuorumExplorerEndpoint.BLOCKS_PATH;
import static com.lykke.tests.api.base.PathConsts.QuorumExplorerEndpoint.BY_HASH_EVENTS_PATH;
import static com.lykke.tests.api.base.PathConsts.QuorumExplorerEndpoint.BY_HASH_PATH;
import static com.lykke.tests.api.base.PathConsts.QuorumExplorerEndpoint.BY_HASH_TRANSACTIONS_PATH;
import static com.lykke.tests.api.base.PathConsts.QuorumExplorerEndpoint.BY_NUMBER_EVENTS_PATH;
import static com.lykke.tests.api.base.PathConsts.QuorumExplorerEndpoint.BY_NUMBER_PATH;
import static com.lykke.tests.api.base.PathConsts.QuorumExplorerEndpoint.BY_NUMBER_TRANSACTIONS_PATH;
import static com.lykke.tests.api.base.PathConsts.QuorumExplorerEndpoint.EVENTS_PATH;
import static com.lykke.tests.api.base.PathConsts.QuorumExplorerEndpoint.TRANSACTION_EVENTS_PATH;
import static com.lykke.tests.api.base.PathConsts.QuorumExplorerEndpoint.TRANSACTION_HASH_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.BY_EMAIL_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.BY_REFERRER_ID_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.CONFIRM_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.GET_BY_EMAIL_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.GET_REFERRAL_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.POST_REFERRAL_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.REFERRAL_HOTELS_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.REFERRAL_LEADS_APPROVED_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.REFERRAL_LEADS_APPROVE_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.REFERRAL_LEADS_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.REFERRAL_LEADS_PROPERTY_PURCHASE_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.REFERRAL_LEADS_STATISTIC_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.REFERRAL_PATH;
import static com.lykke.tests.api.base.PathConsts.ReferralService.USE_PATH;
import static com.lykke.tests.api.base.PathConsts.SmsProviderMockEndpoint.SENT_SMS_PATH;
import static com.lykke.tests.api.base.PathConsts.SmsProviderMockEndpoint.SMS_DETAILS_PATH;
import static com.lykke.tests.api.base.PathConsts.SmsProviderMockEndpoint.SMS_QUERY_PATH;
import static com.lykke.tests.api.base.PathConsts.TokenStatisticsJobEndpoint.BY_DAYS;
import static com.lykke.tests.api.base.PathConsts.TokenStatisticsJobEndpoint.GENERAL_PATH;
import static com.lykke.tests.api.base.PathConsts.TokenStatisticsJobEndpoint.TOKENS_BY_DATE_PATH;
import static com.lykke.tests.api.base.PathConsts.TokenStatisticsJobEndpoint.TOKENS_CURRENT_PATH;
import static com.lykke.tests.api.base.PathConsts.TokenStatisticsJobEndpoint.TOKENS_SNAPSHOT_PATH;
import static com.lykke.tests.api.base.PathConsts.WalletManagementApiEndpoint.BLOCK_STATUS_PATH;
import static com.lykke.tests.api.base.PathConsts.WalletManagementApiEndpoint.BLOCK_WALLET_PATH;
import static com.lykke.tests.api.base.PathConsts.WalletManagementApiEndpoint.TRANSFER_BALANCE_PATH;
import static com.lykke.tests.api.base.PathConsts.WalletManagementApiEndpoint.UNBLOCK_WALLET_PATH;
import static com.lykke.tests.api.base.PathConsts.WalletManagementApiEndpoint.WALLET_MNGMT_PATH;
import static com.lykke.tests.api.base.PathConsts.getBaseUrl;

import com.lykke.tests.api.base.PathConsts.AdminApiService;
import com.lykke.tests.api.base.PathConsts.AdminManagementService;
import com.lykke.tests.api.base.PathConsts.AgentManagementApiEndpoint;
import com.lykke.tests.api.base.PathConsts.BonusEngineEndpoint;
import com.lykke.tests.api.base.PathConsts.CampaignsEndpoint;
import com.lykke.tests.api.base.PathConsts.CredentialsEndpoint;
import com.lykke.tests.api.base.PathConsts.CrossChainTransfersEndpoint;
import com.lykke.tests.api.base.PathConsts.CrossChainWalletLinkerEndpoint;
import com.lykke.tests.api.base.PathConsts.CurrencyConvertorApiEndpoint;
import com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint;
import com.lykke.tests.api.base.PathConsts.CustomerManagementEndpoint;
import com.lykke.tests.api.base.PathConsts.CustomerProfileApiEndpoint;
import com.lykke.tests.api.base.PathConsts.DashboardStatisticsEndpoint;
import com.lykke.tests.api.base.PathConsts.DictionariesEndpoint;
import com.lykke.tests.api.base.PathConsts.EligibilityServicesEndpoint;
import com.lykke.tests.api.base.PathConsts.EthereumBridgeEndpoint;
import com.lykke.tests.api.base.PathConsts.OperationsHistoryApiEndpoint;
import com.lykke.tests.api.base.PathConsts.PartnerApiEndpoint;
import com.lykke.tests.api.base.PathConsts.PartnerManagementEndpoint;
import com.lykke.tests.api.base.PathConsts.PartnersIntegrationEndpoint;
import com.lykke.tests.api.base.PathConsts.PrivateBlockchainFacadeEndpoint;
import com.lykke.tests.api.base.PathConsts.PushNotificationsEndpoint;
import com.lykke.tests.api.base.PathConsts.QuorumExplorerEndpoint;
import com.lykke.tests.api.base.PathConsts.QuorumOperationExecutorEndpoint;
import com.lykke.tests.api.base.PathConsts.ReferralService;
import com.lykke.tests.api.base.PathConsts.ReportingEndpoint;
import com.lykke.tests.api.base.PathConsts.SmsProviderMockEndpoint;
import com.lykke.tests.api.base.PathConsts.TiersEndpoint;
import com.lykke.tests.api.base.PathConsts.VouchersEndpoint;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Paths {

    public static final String CUSTOMER_API_PATH =
            getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CUSTOMER_PATH.getPath();

    public static final String CUSTOMER_API_WALLETS_PATH =
            getBaseUrl(CUSTOMER_API_COMPONENT_URL) + WALLETS_PATH.getPath();

    public static final String CUSTOMER_API_WALLETS_TRANSFER_PATH =
            CUSTOMER_API_WALLETS_PATH + TRANSFER_PATH.getPath();

    public static final String CUSTOMER_API_EMAILS_PATH =
            getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.EMAILS_PATH.getPath() + VERIFICATION
                    .getPath();

    public static final String CUSTOMER_API_OPERATIONS_HISTORY_PATH =
            getBaseUrl(CUSTOMER_API_COMPONENT_URL) + HISTORY_PATH.getPath() + OPERATIONS_PATH
                    .getPath();

    public static final String CUSTOMER_API_AGENTS_PATH =
            getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.AGENTS_PATH
                    .getPath();

    public static final String CUSTOMER_AUTH_API_PATH =
            getBaseUrl(CUSTOMER_API_COMPONENT_URL) + AUTH_PATH.getPath();

    public static final String CUSTOMER_API_COMMON_INFORMATION =
            getBaseUrl(CUSTOMER_API_COMPONENT_URL) + COMMON_INFORMATION.getPath();

    public static final String REFERRALS_API_PATH =
            getBaseUrl(CUSTOMER_API_COMPONENT_URL) + REFERRALS_PATH.getPath();

    public static final String WALLET_MANAGEMENT_API_PATH =
            getBaseUrl(WALLET_MANAGEMENT_API_COMPONENT_URL) + WALLET_MNGMT_PATH.getPath();

    public static final String WALLET_MANAGEMENT_API_TRANSFER_BALANCE_PATH =
            getBaseUrl(WALLET_MANAGEMENT_API_COMPONENT_URL) + WALLET_MNGMT_PATH.getPath() + TRANSFER_BALANCE_PATH
                    .getPath();

    public static final String ADMIN_MANAGEMENT_API_PATH =
            getBaseUrl(ADMIN_MANAGEMENT_COMPONENT_URL) + ADMIN_MANAGEMENT_PATH.getPath();

    public static final String MVN_UBE_INTEGRATION_API_PATH =
            getBaseUrl(MVN_UBE_INTEGRATION_COMPONENT_URL) + MVN_UBE_INTEGRATION_PATH.getPath();

    public static final String NOTIFICATION_ADAPTER_KEYS_API_PATH =
            getBaseUrl(NOTIFICATION_SYSTEM_ADAPTER_URL);

    public static final String REFERRAL_API_REFERRAL_PATH =
            getBaseUrl(REFERRAL_API_COMPONENT_URL) + REFERRALS_PATH.getPath();

    public static final String REFERRAL_API_REFERRAL_LEADS_PATH =
            getBaseUrl(REFERRAL_API_COMPONENT_URL) + REFERRAL_LEADS_PATH.getPath();

    public static final String REFERRAL_API_REFERRAL_LEADS_APPROVED_PATH =
            REFERRAL_API_REFERRAL_LEADS_PATH + REFERRAL_LEADS_APPROVED_PATH.getPath();

    public static final String REFERRAL_API_REFERRAL_LEADS_APPROVE_PATH =
            REFERRAL_API_REFERRAL_LEADS_PATH + REFERRAL_LEADS_APPROVE_PATH.getPath();

    public static final String REFERRAL_API_REFERRAL_LEADS_PROPERTY_PURCHASES_PATH =
            REFERRAL_API_REFERRAL_LEADS_PATH + REFERRAL_LEADS_PROPERTY_PURCHASE_PATH.getPath();

    public static final String REFERRAL_API_GET_REFERRAL_PATH =
            getBaseUrl(REFERRAL_API_COMPONENT_URL) + GET_REFERRAL_PATH.getPath();

    public static final String REFERRAL_API_POST_REFERRAL_PATH =
            getBaseUrl(REFERRAL_API_COMPONENT_URL) + POST_REFERRAL_PATH.getPath();

    public static final String REFERRAL_API_REFERRAL_LEADS_STATISTIC_PATH =
            getBaseUrl(REFERRAL_API_COMPONENT_URL) + REFERRAL_LEADS_PATH.getPath() + REFERRAL_LEADS_STATISTIC_PATH
                    .getPath();

    public static final String EMAIL_MESSAGE_API_PATH =
            getBaseUrl(NOTIFICATION_SYSTEM_BROKER_URL) + EMAIL_MESSAGE_PATH.getPath();
    public static final String PUSH_MESSAGE_API_PATH =
            getBaseUrl(NOTIFICATION_SYSTEM_BROKER_URL) + EMAIL_MESSAGE_PATH.getPath();

    public static final String OPERATIONS_HISTORY_TRANSACTIONS_API_PATH =
            getBaseUrl(OPERATIONS_HISTORY_URL) + TRANSACTIONS_PATH.getPath();

    public static final String OPERATIONS_HISTORY_TRANSFERS_API_PATH =
            getBaseUrl(OPERATIONS_HISTORY_URL) + TRANSFERS_PATH.getPath();

    public static final String CREDENTIALS_API_PATH =
            getBaseUrl(CREDENTIALS_URL) + CREDENTIALS_PATH.getPath();

    public static final String CUSTOMER_API_TRANSFERS_PATH =
            CUSTOMER_API_WALLETS_PATH + CustomerApiEndpoint.TRANSFERS_PATH.getPath();

    public static final String CUSTOMER_API_CHANGE_PASSWORD_PATH =
            CUSTOMER_API_PATH + CHANGE_PASSWORD_PATH.getPath();

    public static final String CUSTOMER_API_RESET_PASSWORD_PATH =
            CUSTOMER_API_PATH + RESET_PASSWORD_PATH.getPath();

    public static final String MVN_PURCHASE_API_PATH =
            getBaseUrl(MVN_INTEGRATION_URL) + PURCHASES_PATH.getPath();

    public static final String OPERATIONS_HISTORY_STATISTICS_PATH =
            getBaseUrl(OPERATIONS_HISTORY_URL) + PathConsts.OperationsHistoryApiEndpoint.STATISTICS_PATH.getPath();

    public static final String OPERATIONS_HISTORY_STATISTICS_ACTIVE_CUSTOMERS_PATH =
            OPERATIONS_HISTORY_STATISTICS_PATH + ACTIVE_CUSTOMERS_PATH.getPath();

    public static final Function<String, String> OPERATIONS_BY_CUSTOMER_ID_API_PATH =
            (id) ->
                    OPERATIONS_HISTORY_TRANSACTIONS_API_PATH + TRANSACTIONS_BY_ID_PATH.getFilledInPath(id);

    public static final String CAMPAIGNS_API_PATH =
            getBaseUrl(CAMPAIGNS_URL) + CAMPAIGNS_PATH.getPath();

    public static final String BONUS_TYPES_API_PATH =
            getBaseUrl(CAMPAIGNS_URL) + BONUS_TYPES_PATH.getPath();

    public static final String PRIVATE_BLOCKCHAIN_NEW_API_PATH =
            getBaseUrl(PRIVATE_BLOCKCHAIN_FACADE_URL) + OPERATIONS_NEW.getPath();
    public static final String PRIVATE_BLOCKCHAIN_ALL_ACCEPTED_API_PATH =
            getBaseUrl(PRIVATE_BLOCKCHAIN_FACADE_URL) + OPERATIONS_ACCEPTED.getPath();
    public static final Function<String, String> PRIVATE_BLOCKCHAIN_ACCEPTED_API_PATH =
            (id) ->
                    getBaseUrl(PRIVATE_BLOCKCHAIN_FACADE_URL) + OPERATIONS_ACCEPTED.getFilledInPath(id);

    public static final Function<String, String> PRIVATE_BLOCKCHAIN_FAILED_API_PATH =
            (hash) ->
                    getBaseUrl(PRIVATE_BLOCKCHAIN_FACADE_URL) + OPERATIONS_FAILED.getFilledInPath(hash);

    public static final Function<String, String> PRIVATE_BLOCKCHAIN_SUCCEEDED_API_PATH =
            (hash) ->
                    getBaseUrl(PRIVATE_BLOCKCHAIN_FACADE_URL) + OPERATIONS_SUCCEEDED.getFilledInPath(hash);

    public static final String PRIVATE_BLOCKCHAIN_WALLETS_API_PATH =
            getBaseUrl(PRIVATE_BLOCKCHAIN_FACADE_URL) + PrivateBlockchainFacadeEndpoint.WALLETS.getPath();

    public static final String PRIVATE_BLOCKCHAIN_TOTAL_AMOUNT_API_PATH =
            getBaseUrl(PRIVATE_BLOCKCHAIN_FACADE_URL) + TOTAL_AMOUNT.getPath();

    public static final String PRIVATE_BLOCKCHAIN_BONUSES_API_PATH =
            getBaseUrl(PRIVATE_BLOCKCHAIN_FACADE_URL) + BONUSES.getPath();
    public static final Function<String, String> PRIVATE_BLOCKCHAIN_CUSTOMER_BALANCE_API_PATH =
            (id) ->
                    getBaseUrl(PRIVATE_BLOCKCHAIN_FACADE_URL) + CUSTOMER_BALANCE.getFilledInPath(id);
    public static final String PRIVATE_BLOCKCHAIN_TRANSFERS_API_PATH =
            getBaseUrl(PRIVATE_BLOCKCHAIN_FACADE_URL) + TRANSFERS.getPath();
    public static final String BONUS_ENGINE_SIMULATE_TRIGGER_API_PATH =
            getBaseUrl(BONUS_ENGINE_URL) + SIMULATE_TRIGGER.getPath();
    public static final String PUSH_NOTIFICATIONS_REGISTRATIONS_API_PATH =
            getBaseUrl(PUSH_NOTIFICATIONS) + REGISTRATIONS.getPath();
    public static final Function<String, String> PUSH_NOTIFICATIONS_CUSTOMER_ID_API_PATH =
            (id) ->
                    getBaseUrl(PUSH_NOTIFICATIONS) + CUSTOMER_ID.getFilledInPath(id);
    public static final Function<String, String> PUSH_NOTIFICATIONS_REGISTRATION_ID_API_PATH =
            (id) ->
                    getBaseUrl(PUSH_NOTIFICATIONS) + REGISTRATION_ID.getFilledInPath(id);

    public static final Function<String, String> PUSH_NOTIFICATIONS_INFOBIP_TOKEN_API_PATH =
            (token) ->
                    getBaseUrl(PUSH_NOTIFICATIONS) + INFOBIP_TOKEN.getFilledInPath(token);

    public static final class AdminApi {

        public static final String ADMIN_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + ADMIN_PATH.getPath();
        @Deprecated
        public static final String ADMIN_API_CUSTOMER_BALANCE_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.CUSTOMER_BALANCE.getPath();
        @Deprecated
        public static final Function<String, String> ADMIN_API_CUSTOMER_WALLET_ADDRESS_PATH =
                (customerId) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + CUSTOMERS.getPath() + AdminApiService.WALLET_ADDRESS_PATH
                                .getFilledInPath(customerId);
        // deprecated?
        public static final Function<String, String> ADMIN_API_CUSTOMER_PUBLIC_WALLET_ADDRESS_BY_ID_PATH =
                (customerId) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + CUSTOMERS.getPath()
                                + AdminApiService.PUBLIC_WALLET_ADDRESS_BY_ID_PATH.getFilledInPath(customerId);
        public static final String ADMIN_API_CUSTOMER_PUBLIC_WALLET_ADDRESS_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + CUSTOMERS.getPath()
                        + AdminApiService.PUBLIC_WALLET_ADDRESS_PATH.getPath();
        @Deprecated
        public static final Function<String, String> ADMIN_API_CUSTOMER_DETAILS_PATH =
                (customerId) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + CUSTOMER.getFilledInPath(customerId);
        @Deprecated
        public static final Function<String, String> ADMIN_API_CAMPAIGN_BY_ID_PATH =
                (campaignId) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + CAMPAIGNS.getPath() + BY_CAMPAIGN_ID
                                .getFilledInPath(campaignId);
        public static final String ADMIN_API_CAMPAIGN_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + CAMPAIGNS.getPath();
        public static final String ADMIN_API_CONDITIONS_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + CONDITIONS_PATH.getPath();
        public static final String BONUS_TYPES_API_PATH = ADMIN_API_CONDITIONS_PATH;
        public static final String ADMIN_API_CUSTOMERS_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + CUSTOMERS.getPath();
        // TODO:
        public static final String ADMIN_API_CUSTOMERS_SEARCH_PATH =
                ADMIN_API_CUSTOMERS_PATH + SEARCH_PATH.getPath();
        public static final String ADMIN_API_CUSTOMERS_HISTORY_PATH =
                ADMIN_API_CUSTOMERS_PATH + CUSTOMER_HISTORY.getPath();
        public static final String ADMIN_API_EMAIL_VERIFICATION_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + EMAIL_VERIFICATION.getPath();
        public static final String ADMIN_STATISTICS_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + PathConsts.AdminApiService.STATISTICS_PATH.getPath();
        public static final String ADMINS_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + ADMINS.getPath();
        public static final String GENERATE_SUGGESTED_PASSWORD_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + ADMINS.getPath()
                        + AdminApiService.GENERATE_SUGGESTED_PASSWORD_PATH.getPath();
        public static final String AUTOFILL_DATA_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + ADMINS.getPath()
                        + AdminApiService.AUTOFILL_DATA_PATH.getPath();
        public static final String BASE_URL_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL);
        public static final String CREDENTIALS_API_PATH =
                getBaseUrl(CREDENTIALS_ADMIN_COMPONENT_URL) + CREDENTIALS_ADMIN_PATH.getPath();

        public static final String UNPROCESSED_PAYMENTS_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.PAYMENTS_PATH.getPath() + UNPROCESSED_PAYMENT_PATH
                        .getPath();
        @Deprecated
        public static final Function<String, String> ACCEPT_UNPROCESSED_PAYMENTS_PATH =
                (paymentId) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.PAYMENTS_PATH.getPath()
                                + ACCEPT_PAYMENT_PATH.getFilledInPath(paymentId);
        // TODO:
        @Deprecated
        public static final Function<String, String> REJECTED_PAYMENTS_API_PATH =
                (paymentId) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.PAYMENTS_PATH.getPath()
                                + AdminApiService.REJECTED_PAYMENT_PATH.getFilledInPath(paymentId);

        @Deprecated
        public static final Function<String, String> BLOCK_CUSTOMER_BY_ID_PATH =
                (customerId) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + BLOCK_CUSTOMER.getFilledInPath(customerId);
        @Deprecated
        public static final Function<String, String> UNBLOCK_CUSTOMER_BY_ID_PATH =
                (customerId) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + UNBLOCK_CUSTOMER.getFilledInPath(customerId);
        @Deprecated
        public static final Function<String, String> BLOCK_CUSTOMER_WALLET_BY_ID_PATH =
                (customerId) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + BLOCK_CUSTOMER_WALLET.getFilledInPath(customerId);
        @Deprecated
        public static final Function<String, String> UNBLOCK_CUSTOMER_WALLET_BY_ID_PATH =
                (customerId) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + UNBLOCK_CUSTOMER_WALLET.getFilledInPath(customerId);

        public static final String ADMIN_API_BURN_RULES_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.BURN_RULES.getPath();
        public static final String VOUCHERS_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL)
                        + AdminApiService.BURN_RULES.getPath()
                        + AdminApiService.VOUCHERS_PATH.getPath();
        public static final String ADMIN_API_BURN_RULES_IMAGE_API_PATH =
                ADMIN_API_BURN_RULES_API_PATH + IMAGE_PATH.getPath();
        public static final String ADMIN_DASHBOARD_STATISTICS_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + DASHBOARD_PATH.getPath();
        public static final String ADMIN_PARTNERS_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.PARTNERS_PATH.getPath();
        public static final String ADMIN_GENERATE_CLIENT_SECRET_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.PARTNERS_PATH.getPath()
                        + GENERATE_CLIENT_SECRET_PATH.getPath();
        public static final String ADMIN_GENERATE_CLIENT_ID_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.PARTNERS_PATH.getPath()
                        + GENERATE_CLIENT_ID_PATH.getPath();
        public static final String BLOCKS_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.BLOCKS_PATH.getPath();
        @Deprecated
        public static final Function<Long, String> BLOCK_BY_NUMBER_API_PATH =
                (number) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.BLOCKS_PATH.getPath()
                                + AdminApiService.BY_NUMBER_PATH.getFilledInPath(String.valueOf(number));
        @Deprecated
        public static final Function<String, String> BLOCK_BY_HASH_API_PATH =
                (hash) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.BLOCKS_PATH.getPath()
                                + AdminApiService.BY_HASH_PATH.getFilledInPath(hash);
        public static final String TRANSACTIONS_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.TRANSACTIONS_PATH.getPath();
        @Deprecated
        public static final Function<Long, String> TRANSACTIONS_BY_BLOCK_NUMBER_API_PATH =
                (blockNumber) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.BLOCKS_PATH.getPath()
                                + AdminApiService.BY_NUMBER_TRANSACTIONS_PATH
                                .getFilledInPath(String.valueOf(blockNumber));
        @Deprecated
        public static final Function<String, String> TRANSACTIONS_BY_BLOCK_HASH_API_PATH =
                (blockHash) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.BLOCKS_PATH.getPath()
                                + AdminApiService.BY_HASH_TRANSACTIONS_PATH.getFilledInPath(blockHash);
        public static final String EVENTS_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.EVENTS_PATH.getPath();
        @Deprecated
        public static final Function<Long, String> EVENTS_BY_BLOCK_NUMBER_API_PATH =
                (blockNumber) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.BLOCKS_PATH.getPath()
                                + AdminApiService.BY_NUMBER_EVENTS_PATH.getFilledInPath(String.valueOf(blockNumber));
        @Deprecated
        public static final Function<String, String> EVENTS_BY_BLOCK_HASH_API_PATH =
                (blockHash) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.BLOCKS_PATH.getPath()
                                + AdminApiService.BY_HASH_EVENTS_PATH.getFilledInPath(blockHash);
        @Deprecated
        public static final Function<String, String> TRANSACTION_EVENTS_API_PATH =
                (hash) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.TRANSACTIONS_PATH.getPath()
                                + String.format("/%s", hash)
                                + AdminApiService.TRANSACTION_EVENTS_PATH.getPath();
        @Deprecated
        public static final Function<String, String> TRANSACTION_DETAILS_BY_HASH_API_PATH =
                (hash) ->
                        getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.TRANSACTIONS_PATH.getPath()
                                + AdminApiService.TRANSACTION_HASH_PATH.getFilledInPath(hash);
        // TODO:
        public static final String SETTINGS_GLOBAL_CURRENCY_RATE_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.SETTINGS_PATH.getPath()
                        + AdminApiService.GLOBAL_CURRENCY_RATE_PATH.getPath();
        // TODO:
        public static final String SETTINGS_AGENT_REQUIREMENTS_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.SETTINGS_PATH.getPath()
                        + AdminApiService.AGENT_REQUIREMENTS_PATH.getPath();

        public static final String SETTINGS_OPERATION_FEES_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.SETTINGS_PATH.getPath()
                        + AdminApiService.OPERATION_FEES_PATH.getPath();

        public static final String CUSTOMERS_SEARCH_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + CUSTOMERS.getPath() + SEARCH_PATH.getPath();
        public static final String ADMINS_SEARCH_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + ADMINS.getPath() + SEARCH_PATH.getPath();

        public static final String REPORTS_API_PATH =
                getBaseUrl(ADMIN_API_COMPONENT_URL) + REPORTS_PATH.getPath();

        public static final class InputValidation {

            @Deprecated
            public static final BiFunction<Long, Long, String> BLOCK_BY_NUMBER_VALIDATION_API_PATH =
                    (number, shift) ->
                            getBaseUrl(ADMIN_API_COMPONENT_URL) + AdminApiService.BLOCKS_PATH.getPath()
                                    + AdminApiService.BY_NUMBER_PATH
                                    .getFilledInPath(String.valueOf(number + shift));
        }
    }

    public static final class AdminManagement {

        public static final String ADMIN_BY_EMAIL_API_PATH =
                getBaseUrl(ADMIN_MANAGEMENT_COMPONENT_URL) + ADMIN_MANAGEMENT_PATH.getPath()
                        + PathConsts.AdminManagementService.GET_BY_EMAIL.getPath();
        public static final String AUTO_FILL_VALUES_API_PATH =
                getBaseUrl(ADMIN_MANAGEMENT_COMPONENT_URL) + ADMIN_MANAGEMENT_PATH.getPath() + AUTO_FILL_VALUES_PATH
                        .getPath();
        public static final String ADMIN_REGISTER_API_PATH =
                getBaseUrl(ADMIN_MANAGEMENT_COMPONENT_URL) + ADMIN_MANAGEMENT_PATH.getPath() + REGISTER.getPath();
        public static final String UPDATE_API_PATH =
                getBaseUrl(ADMIN_MANAGEMENT_COMPONENT_URL) + ADMIN_MANAGEMENT_PATH.getPath()
                        + AdminManagementService.UPDATE_PATH.getPath();
        public static final String UPDATE_PERMISSIONS_API_PATH =
                getBaseUrl(ADMIN_MANAGEMENT_COMPONENT_URL) + ADMIN_MANAGEMENT_PATH.getPath()
                        + AdminManagementService.UPDATE_PERMISSIONS_PATH.getPath();
        public static final String GET_PERMISSIONS_API_PATH =
                getBaseUrl(ADMIN_MANAGEMENT_COMPONENT_URL) + ADMIN_MANAGEMENT_PATH.getPath()
                        + AdminManagementService.GET_PERMISSIONS_PATH.getPath();
        public static final String ADMIN_LOGIN_API_PATH =
                getBaseUrl(ADMIN_MANAGEMENT_COMPONENT_URL) + AdminManagementService.AUTH_PATH.getPath()
                        + AdminManagementService.LOGIN.getPath();
        public static final String GET_ADMIN_USERSP_API_PATH =
                getBaseUrl(ADMIN_MANAGEMENT_COMPONENT_URL) + ADMIN_MANAGEMENT_PATH.getPath() + GET_ADMIN_USERS
                        .getPath();
    }

    public static final class AgentManagement {

        public static final String AGENTS_API_PATH =
                getBaseUrl(AGENT_MANAGEMENT) + AgentManagementApiEndpoint.AGENTS_PATH.getPath();
        public static final String AGENTS_LIST_API_PATH =
                getBaseUrl(AGENT_MANAGEMENT) + AgentManagementApiEndpoint.AGENTS_PATH.getPath()
                        + AgentManagementApiEndpoint.LIST_PATH.getPath();

        public static final String REQUIREMENTS_API_PATH =
                getBaseUrl(AGENT_MANAGEMENT) + REQUIREMENTS_PATH.getPath();

        public static final String REQUIREMENTS_TOKENS_API_PATH =
                getBaseUrl(AGENT_MANAGEMENT) + REQUIREMENTS_TOKENS_PATH.getPath();
    }

    public static final class BonusEngine {

        public static final BiFunction<String, String, String> CAMPAIGN_COMPLETION_API_PATH =
                (customerId, campaignId) ->
                        getBaseUrl(BONUS_ENGINE_URL) + String
                                .format(BonusEngineEndpoint.CAMPAIGN_COMPLETION_PATH.getPath(), customerId, campaignId);
    }

    public static final class Campaigns {

        public static final String BURN_RULES_API_PATH =
                getBaseUrl(CAMPAIGNS_URL) + BURN_RULES_PATH.getPath();
        public static final Function<String, String> BURN_RULE_BY_ID_API_PATH =
                (id) ->
                        getBaseUrl(CAMPAIGNS_URL) + BURN_RULES_PATH.getPath() + String
                                .format(BURN_RULE_ID.getFilledInPath(id));
        public static final String MOBILE_BURN_RULES_API_PATH =
                getBaseUrl(CAMPAIGNS_URL) + MOBILE_BURN_RULES_PATH.getPath();
        public static final Function<String, String> MOBILE_BURN_RULES_BY_ID_API_PATH =
                (burnRuleId) ->
                        getBaseUrl(CAMPAIGNS_URL)
                                + MOBILE_BURN_RULES_PATH.getPath()
                                + CampaignsEndpoint.CAMPAIGN_BY_ID_PATH.getFilledInPath(burnRuleId);
        public static final String MOBILE_EARN_RULES_API_PATH =
                getBaseUrl(CAMPAIGNS_URL) + MOBILE_EARN_RULES_PATH.getPath();

        public static final String EARN_RULE_IMAGE_API_PATH =
                getBaseUrl(CAMPAIGNS_URL) + CAMPAIGNS_PATH.getPath() + IMAGE_PATH.getPath();
    }

    public static final class Credentials {

        public static final String CREDENTIALS_API_PATH =
                getBaseUrl(CREDENTIALS_URL) + CREDENTIALS_PATH.getPath();
        public static final String PARTNERS_API_PATH =
                getBaseUrl(CREDENTIALS_URL) + PARTNERS_PATH.getPath();
        public static final String PARTNERS_VALIDATE_API_PATH =
                getBaseUrl(CREDENTIALS_URL) + PARTNERS_VALIDATE_PATH.getPath();
        public static final String CREDENTIALS_CLIENT_ID_API_PATH =
                getBaseUrl(CREDENTIALS_URL) + CREDENTIALS_PATH.getPath() + CredentialsEndpoint.CLIENT_ID_PATH.getPath();
        public static final String CREDENTIALS_CLIENT_SECRET_API_PATH =
                getBaseUrl(CREDENTIALS_URL) + CREDENTIALS_PATH.getPath() + CredentialsEndpoint.CLIENT_SECRET_PATH
                        .getPath();
        public static final String PIN_API_PATH =
                getBaseUrl(CREDENTIALS_URL) + CREDENTIALS_PATH.getPath() + CredentialsEndpoint.PIN_PATH.getPath();
        public static final String VALIDATE_PIN_API_PATH =
                getBaseUrl(CREDENTIALS_URL) + CREDENTIALS_PATH.getPath()
                        + CredentialsEndpoint.VALIDATE_PIN_PATH.getPath();
        public static final String HAS_PIN_API_PATH =
                getBaseUrl(CREDENTIALS_URL) + CREDENTIALS_PATH.getPath() + CredentialsEndpoint.HAS_PIN_PATH.getPath();
    }

    public static final class CrossChainWalletLinker {

        public static final Function<String, String> CUSTOMER_PUBLIC_ADDRESS_API_PATH =
                (customerId) ->
                        getBaseUrl(CROSS_CHAIN_WALLET_LINKER_URL)
                                + CrossChainWalletLinkerEndpoint.CUSTOMERS_PATH.getPath()
                                + CrossChainWalletLinkerEndpoint.CUSTOMER_PUBLIC_ADDRESS_PATH
                                .getFilledInPath(customerId);
        public static final Function<String, String> CUSTOMER_NEXT_FEE_API_PATH =
                (customerId) ->
                        getBaseUrl(CROSS_CHAIN_WALLET_LINKER_URL)
                                + CrossChainWalletLinkerEndpoint.CUSTOMERS_PATH.getPath()
                                + CrossChainWalletLinkerEndpoint.CUSTOMER_NEXT_FEE_PATH.getFilledInPath(customerId);
        public static final String LINK_REQUESTS_API_PATH =
                getBaseUrl(CROSS_CHAIN_WALLET_LINKER_URL) + CrossChainWalletLinkerEndpoint.LINK_REQUESTS_PATH
                        .getPath();
        public static final String LINK_REQUEST_APPROVAL_API_PATH =
                getBaseUrl(CROSS_CHAIN_WALLET_LINKER_URL) + CrossChainWalletLinkerEndpoint.LINK_REQUESTS_PATH.getPath()
                        + CrossChainWalletLinkerEndpoint.LINK_REQUEST_APPROVAL_PATH.getPath();

        public static final String CONFIGURATION_API_PATH =
                getBaseUrl(CROSS_CHAIN_WALLET_LINKER_URL) + CrossChainWalletLinkerEndpoint.CONFIGURATION_PATH.getPath();
        public static final Function<String, String> CONFIGURATION_TYPE_API_PATH =
                (type) ->
                        getBaseUrl(CROSS_CHAIN_WALLET_LINKER_URL)
                                + CrossChainWalletLinkerEndpoint.CONFIGURATION_PATH.getPath()
                                + CrossChainWalletLinkerEndpoint.CONFIGURATION_TYPE_PATH.getFilledInPath(type);
    }

    public static final class CrossChainTransfers {

        public static final String TRANSFER_TO_EXTERNAL_API_PATH =
                getBaseUrl(CROSS_CHAIN_TRANSFERS_URL) + CrossChainTransfersEndpoint.CROSS_CHAIN_TRANSFERS_PATH.getPath()
                        + CrossChainTransfersEndpoint.TO_EXTERNAL_PATH.getPath();
    }

    public static final class CurrencyConverter {

        public static final String CURRENCY_CONVERTER_API_PATH =
                getBaseUrl(CURRENCY_CONVERTOR_URL) + CurrencyConvertorApiEndpoint.CONVERTER_PATH.getPath();
        public static final String CURRENCY_RATES_API_PATH =
                getBaseUrl(CURRENCY_CONVERTOR_URL) + CURRENCY_RATES_PATH.getPath();
        public static final String GLOBAL_CURRENCY_RATES_API_PATH =
                getBaseUrl(CURRENCY_CONVERTOR_URL) + CurrencyConvertorApiEndpoint.GLOBAL_CURRENCY_RATES_PATH.getPath();
    }

    public static final class Customer {

        public static final String SPEND_RULES_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + SPEND_RULES_PATH.getPath();
        @Deprecated
        public static final Function<String, String> SPEND_RULE_BY_ID_API_PATH =
                (spendRuleId) ->
                        getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                                + SPEND_RULES_PATH.getPath()
                                + CustomerApiEndpoint.BY_ID.getFilledInPath(spendRuleId);
        public static final String SPEND_RULES_SEARCH_BY_ID_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                        + SPEND_RULES_PATH.getPath()
                        + CustomerApiEndpoint.SEARCH_PATH.getPath();
        public static final String LIST_COUNTRIES_OF_RESIDENCE_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + LIST_COUNTRIES_OF_RESIDENCE_PATH.getPath();

        public static final String LIST_COUNTRY_PHONE_CODES_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + LIST_COUNTRY_PHONE_CODES_PATH.getPath();

        public static final String GOOGLE_REGISTER_API_PATH =
                CUSTOMER_API_PATH + GOOGLE_REGISTER_PATH.getPath();

        public static final String GOOGLE_LOGIN_API_PATH =
                CUSTOMER_AUTH_API_PATH + GOOGLE_LOGIN_PATH.getPath();
        public static final String VALIDATE_RESET_PASSWORD_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CUSTOMER_PATH.getPath() + VALIDATE_RESET_PASSWORD_PATH
                        .getPath();
        public static final String PASSWORD_VALIDATION_RULES_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CUSTOMER_PATH.getPath() + PASSWORD_VALIDATION_RULES_PATH
                        .getPath();
        public static final String CUSTOMER_API_RESET_PASSWORD_PATH =
                CUSTOMER_API_PATH + RESET_PASSWORD_PATH.getPath();

        public static final String EARN_RULES_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + EARN_RULES_PATH.getPath();
        @Deprecated
        public static final Function<String, String> EARN_RULE_BY_ID_API_PATH =
                (earnRuleId) ->
                        getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                                + EARN_RULES_PATH.getPath()
                                + CustomerApiEndpoint.BY_ID.getFilledInPath(earnRuleId);
        public static final String EARN_RULE_SEARCH_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                        + EARN_RULES_PATH.getPath()
                        + CustomerApiEndpoint.SEARCH_PATH.getPath();
        @Deprecated
        public static final Function<String, String> EARN_RULE_BY_ID_STAKING_API_PATH =
                (earnRuleId) ->
                        getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                                + EARN_RULES_PATH.getPath()
                                + CustomerApiEndpoint.BY_ID.getFilledInPath(earnRuleId)
                                + CustomerApiEndpoint.STAKING_PATH.getPath();
        public static final String EARN_RULE_STAKING_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                        + EARN_RULES_PATH.getPath()
                        + CustomerApiEndpoint.STAKING_PATH.getPath();
        public static final String VERIFY_EMAIL_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.EMAILS_PATH.getPath()
                        + VERIFY_EMAIL_PATH.getPath();

        public static final String TRANSFER_PAYMENT_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + WALLETS_PATH.getPath() + TRANSFER_PAYMENT_PATH.getPath();
        public static final String LINK_REQUEST_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + WALLETS_PATH.getPath() + LINK_REQUEST_PATH.getPath();
        public static final String EXTERNAL_TRANSFER_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + WALLETS_PATH.getPath() + EXTERNAL_TRANSFER_PATH.getPath();
        public static final String NEXT_FEE_API_PATH =
                LINK_REQUEST_API_PATH + NEXT_FEE_PATH.getPath();

        public static final String PAYMENTS_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.PARTNERS_PATH.getPath()
                        + CustomerApiEndpoint.PAYMENTS_PATH.getPath();

        public static final String PAYMENTS_APPROVAL_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.PARTNERS_PATH.getPath()
                        + PAYMENTS_APPROVAL_PATH.getPath();
        public static final String PAYMENTS_REJECTION_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.PARTNERS_PATH.getPath()
                        + PAYMENTS_REJECTION_PATH.getPath();
        public static final String PAYMENTS_PENDING_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.PARTNERS_PATH.getPath()
                        + PAYMENTS_PENDING_PATH.getPath();
        public static final String PAYMENTS_SUCCEEDED_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.PARTNERS_PATH.getPath()
                        + PAYMENTS_SUCCEEDED_PATH.getPath();
        public static final String PAYMENTS_FAILED_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.PARTNERS_PATH.getPath()
                        + PAYMENTS_FAILED_PATH.getPath();
        @Deprecated
        public static final Function<String, String> CUSTOMER_API_PARTNER_MESSAGES_BY_ID_API_PATH =
                (messageId) ->
                        getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.PARTNERS_PATH.getPath()
                                + CustomerApiEndpoint.MESSAGES_BY_ID_PATH.getFilledInPath(messageId);
        public static final String CUSTOMER_API_PARTNER_MESSAGES_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.PARTNERS_PATH.getPath()
                        + CustomerApiEndpoint.MESSAGES_PATH.getPath();

        public static final String CUSTOMER_LOGIN_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + AUTH_PATH.getPath() + CustomerApiEndpoint.LOGIN.getPath();
        public static final String REGISTER_CUSTOMER_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CUSTOMERS.getPath() + CustomerApiEndpoint.REGISTER.getPath();

        public static final String CUSTOMER_PHONES_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.PHONES_PATH.getPath();
        public static final String CUSTOMER_PHONES_VERIFY_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.PHONES_VERIFY_PATH.getPath();

        @Deprecated
        public static final Function<String, String> PUSH_NOTIFICATION_REGISTRATIONS_BY_ID_API_PATH =
                (infobipPushRegistrattionId) ->
                        getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                                + CustomerApiEndpoint.PUSH_NOTIFICATIONA_REGISTRATIONS_PATH.getPath()
                                + CustomerApiEndpoint.BY_ID.getFilledInPath(infobipPushRegistrattionId);
        public static final String PUSH_NOTIFICATIONA_REGISTRATIONS_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                        + CustomerApiEndpoint.PUSH_NOTIFICATIONA_REGISTRATIONS_PATH.getPath();

        public static final String GET_CUSTOMER_WALLET_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                        + WALLETS_PATH.getPath()
                        + GET_CUSTOMER_WALLET.getPath();

        public static final String REFERRAL_HOTELS_ALL_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                        + REFERRALS_PATH.getPath()
                        + CustomerApiEndpoint.HOTELS_PATH.getPath();

        public static final String REFERRALS_ALL_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                        + REFERRALS_PATH.getPath()
                        + CustomerApiEndpoint.REFERRALS_ALL_PATH.getPath();

        public static final String PIN_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                        + CUSTOMER_PATH.getPath()
                        + CustomerApiEndpoint.PIN_PATH.getPath();
        public static final String PIN_CHECK_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                        + CUSTOMER_PATH.getPath()
                        + CustomerApiEndpoint.PIN_CHECK_PATH.getPath();

        public static final String MOBILE_SETTINGS_API_PATH =
                getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.MOBILE_PATH.getPath()
                        + CustomerApiEndpoint.SETTINGS_PATH.getPath();

        public static final class Vouchers {

            public static final String VOUCHERS_API_PATH =
                    getBaseUrl(CUSTOMER_API_COMPONENT_URL) + CustomerApiEndpoint.VOUCHERS_PATH.getPath();
            public static final String BUY_VOUCHERS_API_PATH =
                    getBaseUrl(CUSTOMER_API_COMPONENT_URL)
                            + CustomerApiEndpoint.VOUCHERS_PATH.getPath()
                            + CustomerApiEndpoint.BUY_PATH.getPath();
        }
    }

    public static final class CustomerBonusProfile {

        public static final Function<String, String> CUSTOMER_AGGREGATIONS_PATH =
                (customerId) ->
                        getBaseUrl(BONUS_CUSTOMER_PROFILE_URL) + AGGREGATIONS.getPath() + CUSTOMER_BY_ID_PATH
                                .getFilledInPath(customerId);
    }

    public static final class CustomerManagement {

        public static final String AUTH_API_PATH =
                getBaseUrl(CUSTOMER_MANAGEMENT_COMPONENT_URL) + CUSTOMERMANAGEMENT_AUTH_PATH.getPath();
        public static final String CUSTOMERS_API_PATH =
                getBaseUrl(CUSTOMER_MANAGEMENT_COMPONENT_URL) + CUSTOMERMANAGEMENT_CUSTOMERS_PATH.getPath();
        public static final String EMAILS_API_PATH =
                getBaseUrl(CUSTOMER_MANAGEMENT_COMPONENT_URL) + CustomerManagementEndpoint.EMAILS_PATH.getPath();
        public static final String EMAIL_VERIFICATION_API_PATH =
                getBaseUrl(CUSTOMER_MANAGEMENT_COMPONENT_URL) + CustomerManagementEndpoint.EMAILS_PATH.getPath()
                        + CustomerManagementEndpoint.VERIFICATION.getPath();
        public static final String REGISTER_CUSTOMER_API_PATH =
                CustomerManagement.CUSTOMERS_API_PATH + CustomerManagementEndpoint.REGISTER.getPath();
        public static final String GENERATE_VERIFICATION_API_PATH =
                getBaseUrl(CUSTOMER_MANAGEMENT_COMPONENT_URL) + CustomerManagementEndpoint.PHONES_PATH.getPath()
                        + CustomerManagementEndpoint.GENERATE_VERIFICATION_PATH.getPath();
    }

    public static final class CustomerProfile {

        public static final String CUSTOMER_PROFILE_API_PATH =
                getBaseUrl(CUSTOMER_PROFILE_URL) + ENCRYPTION_KEY_PATH.getPath();
        public static final String CUSTOMER_PROFILE_API_CUSTOMERS_PATH =
                getBaseUrl(CUSTOMER_PROFILE_URL) + CUSTOMER_PROFILE_PATH.getPath();
        public static final String CUSTOMER_PROFILE_CUSTOMERS_IDS_PATH =
                getBaseUrl(CUSTOMER_PROFILE_URL) + CUSTOMER_PROFILE_PATH.getPath() + CUSTOMERS_IDS.getPath();
        public static final String CUSTOMER_PROFILE_API_STATISTICS_PATH =
                getBaseUrl(CUSTOMER_PROFILE_URL) + AdminApiService.STATISTICS_PATH.getPath();
        public static final String CUSTOMER_PROFILE_CONTRIBUTIONS_API_PATH =
                getBaseUrl(BONUS_CUSTOMER_PROFILE_URL) + CONTRIBUTIONS.getPath();
        public static final String CUSTOMER_PROFILE_STATISTICS_API_PATH =
                getBaseUrl(CUSTOMER_PROFILE_URL) + PathConsts.CustomerProfileApiEndpoint.STATISTICS_PATH.getPath();
        public static final Function<String, String> CUSTOMER_PROFILE_SET_EMAIL_AS_VERIFIED_API_PATH =
                (id) ->
                        CUSTOMER_PROFILE_API_CUSTOMERS_PATH + SET_EMAIL_AS_VERIFIED_PATH.getFilledInPath(id);
        public static final Function<String, String> CUSTOMER_PROFILE_BY_ID_API_PATH =
                (id) ->
                        CUSTOMER_PROFILE_API_CUSTOMERS_PATH + CUSTOMER_PROFILE_BY_ID_PATH.getFilledInPath(id);
        public static final Function<String, String> CUSTOMER_PROFILE_BY_PHONE_API_PATH =
                (phone) ->
                        CUSTOMER_PROFILE_API_CUSTOMERS_PATH + BY_PHONE.getFilledInPath(phone);
        public static final String CUSTOMER_PROFILE_API_PARTNER_CONTACTS_PATH =
                getBaseUrl(CUSTOMER_PROFILE_URL) + PARTNER_CONTACTS_PATH.getPath();
        public static final String PARTNER_CONTACTS_PAGINATED =
                CUSTOMER_PROFILE_API_PARTNER_CONTACTS_PATH + PAGINATED_PATH.getPath();
        public static final Function<String, String> PARTNER_CONTACTS_BY_LOCATION =
                (id) ->
                        CUSTOMER_PROFILE_API_PARTNER_CONTACTS_PATH + BY_LOCATION.getFilledInPath(id);
        public static final String CUSTOMER_PHONES_API_PATH =

                getBaseUrl(CUSTOMER_PROFILE_URL) + CustomerProfileApiEndpoint.PHONES_PATH.getPath();
        public static final String CUSTOMER_PHONES_VERIFY_API_PATH =
                getBaseUrl(CUSTOMER_PROFILE_URL) + CustomerProfileApiEndpoint.PHONES_VERIFY_PATH.getPath();
        public static final String ADMIN_PROFILES_API_PATH =
                getBaseUrl(CUSTOMER_PROFILE_URL) + CustomerProfileApiEndpoint.ADMIN_PROFILES_PATH.getPath();
        public static final Function<String, String> ADMIN_PROFILE_API_PATH =
                (adminId) ->
                        getBaseUrl(CUSTOMER_PROFILE_URL) + CustomerProfileApiEndpoint.ADMIN_PROFILES_PATH.getPath()
                                + CustomerProfileApiEndpoint.ADMIN_ID_PATH.getFilledInPath(adminId);
    }

    public static final class DashboardStatistics {

        public static final String IS_ALIVE_API_PATH =
                getBaseUrl(DASHBOARD_STATISTICS_URL) + IS_ALIVE_PATH.getPath();
        public static final String LEADS_API_PATH =
                getBaseUrl(DASHBOARD_STATISTICS_URL) + DashboardStatisticsEndpoint.LEADS_PATH.getPath();
        public static final String CUSTOMERS_API_PATH =
                getBaseUrl(DASHBOARD_STATISTICS_URL) + DashboardStatisticsEndpoint.CUSTOMERS_PATH.getPath();
        public static final String TOKENS_API_PATH =
                getBaseUrl(DASHBOARD_STATISTICS_URL) + TOKENS_PATH.getPath();
    }

    public static final class Dictionaries {

        public static final String COUNTRIES_API_PATH =
                getBaseUrl(DICTIONARIES_API_COMPONENT_URL) + SALESFORCE_PATH.getPath() + COUNTRIES_OF_RESIDENCE_PATH
                        .getPath();
        public static final String COUNTRY_PHONE_CODES_API_PATH =
                getBaseUrl(DICTIONARIES_API_COMPONENT_URL) + SALESFORCE_PATH.getPath() + COUNTRY_PHONE_CODES_PATH
                        .getPath();
        public static final Function<String, String> COUNTRY_BY_ID_API_PATH =
                (id) ->
                        getBaseUrl(DICTIONARIES_API_COMPONENT_URL) + SALESFORCE_PATH.getPath()
                                + COUNTRIES_OF_RESIDENCE_PATH
                                .getPath() + String.format(BY_ID.getPath(), id);
        public static final Function<String, String> COUNTRY_PHONE_CODE_BY_ID_API_PATH =
                (id) ->
                        getBaseUrl(DICTIONARIES_API_COMPONENT_URL) + SALESFORCE_PATH.getPath()
                                + COUNTRY_PHONE_CODES_PATH
                                .getPath() + String.format(BY_ID.getPath(), id);
        public static final String COMMON_INFORMATION_API_PATH =
                getBaseUrl(DICTIONARIES_API_COMPONENT_URL) + DictionariesEndpoint.COMMON_INFORMATION_PATH.getPath();
    }

    public static final class EligibilityServices {

        public static final String CONVERSION_RATE_API_PATH =
                getBaseUrl(ELIGIBILITY_ENGINE_SERVICES_URL) + EligibilityServicesEndpoint.CONVERSION_RATE_PATH
                        .getPath();
        public static final String PARTNER_RATE_API_PATH =
                getBaseUrl(ELIGIBILITY_ENGINE_SERVICES_URL) + EligibilityServicesEndpoint.CONVERSION_RATE_PATH
                        .getPath() + EligibilityServicesEndpoint.PARTNER_RATE_PATH.getPath();
        public static final String EARN_RULE_RATE_API_PATH =
                getBaseUrl(ELIGIBILITY_ENGINE_SERVICES_URL) + EligibilityServicesEndpoint.CONVERSION_RATE_PATH
                        .getPath() + EligibilityServicesEndpoint.EARN_RULE_RATE_PATH.getPath();
        public static final String SPEND_RULE_RATE_API_PATH =
                getBaseUrl(ELIGIBILITY_ENGINE_SERVICES_URL) + EligibilityServicesEndpoint.CONVERSION_RATE_PATH
                        .getPath() + EligibilityServicesEndpoint.SPEND_RULE_RATE_PATH.getPath();
        public static final String PARTNER_AMOUNT_API_PATH =
                getBaseUrl(ELIGIBILITY_ENGINE_SERVICES_URL) + EligibilityServicesEndpoint.CONVERSION_RATE_PATH
                        .getPath() + EligibilityServicesEndpoint.PARTNER_AMOUNT_PATH.getPath();
        public static final String EARN_RULE_AMOUNT_API_PATH =
                getBaseUrl(ELIGIBILITY_ENGINE_SERVICES_URL) + EligibilityServicesEndpoint.CONVERSION_RATE_PATH
                        .getPath() + EligibilityServicesEndpoint.EARN_RULE_AMOUNT_PATH.getPath();
        public static final String SPEND_RULE_AMOUNT_API_PATH =
                getBaseUrl(ELIGIBILITY_ENGINE_SERVICES_URL) + EligibilityServicesEndpoint.CONVERSION_RATE_PATH
                        .getPath() + EligibilityServicesEndpoint.SPEND_RULE_AMOUNT_PATH.getPath();
        public static final String CONDITION_AMOUNT_API_PATH =
                getBaseUrl(ELIGIBILITY_ENGINE_SERVICES_URL) + EligibilityServicesEndpoint.CONVERSION_RATE_PATH
                        .getPath() + EligibilityServicesEndpoint.CONDITION_AMOUNT_PATH.getPath();
        public static final String CONDITION_RATE_API_PATH =
                getBaseUrl(ELIGIBILITY_ENGINE_SERVICES_URL) + EligibilityServicesEndpoint.CONVERSION_RATE_PATH
                        .getPath() + EligibilityServicesEndpoint.CONDITION_RATE_PATH.getPath();
    }

    public static final class MAVNPropertyIntegration {

        public static final String LEADS_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + LEADS_PATH.getPath();
        public static final String HISTORY_REGISTERED_LEADS_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + REGISTERED_LEADS_PATH.getPath();
        public static final String HISTORY_APPROVED_LEADS_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + APPROVED_LEADS_PATH.getPath();
        public static final String HISTORY_PURCHASES_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + PROPERTY_PURCHASES_PATH.getPath();
        public static final String MANUAL_REGISTERED_LEADS_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + REGISTERED_LEADS_MANUAL_PATH.getPath();
        public static final String MANUAL_APPROVED_LEADS_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + APPROVED_LEADS_MANUAL_PATH.getPath();
        public static final String MANUAL_LEADS_CHANGED_SALESMEN_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + LEADS_CHANGED_SALESMEN_MANUAL_PATH.getPath();
        public static final String MANUAL_PURCHASES_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + PROPERTY_PURCHASES_MANUAL_PATH.getPath();
        public static final String REGISTERED_AGENTS_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + REGISTERED_AGENTS_PATH.getPath();
        public static final String PROCESSED_AGENTS_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + PROCESSED_AGENTS_PATH.getPath();
        public static final String MANUAL_AGENTS_CHANGED_SALESMEN_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + AGENTS_CHANGED_SALESMEN_MANUAL_PATH.getPath();
        public static final Function<String, String> SALESMEN_API_PATH =
                (salesforceId) ->
                        getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + SALESMAN_SALESFORCE_ID
                                .getFilledInPath(salesforceId);
        public static final String AGENTS_CHANGED_SALESMEN_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + AGENTS_CHANGED_SALESMEN_PATH.getPath();
        public static final String LEADS_CHANGED_SALESMEN_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + LEADS_CHANGED_SALESMEN_PATH.getPath();
        // these two endpoints are temporary (FAL-1340)
        public static final String MANUAL_REGISTERED_AGENTS_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + REGISTERED_AGENTS_MANUAL_PATH.getPath();
        public static final String MANUAL_PROCESSED_AGENTS_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + PROCESSED_AGENTS_MANUAL_PATH.getPath();
        public static final String MANUAL_PAID_INVOICES_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + PAID_INVOICES_MANUAL_PATH.getPath();
        public static final String HISTORY_PAID_INVOICES_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + PAID_INVOICES_HISTORY_PATH.getPath();
        public static final String INTEGRATION_PAID_INVOICES_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + PAID_INVOICES_INTEGRATION_PATH.getPath();
        public static final String PAID_INVOICES_PAYMENTS_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + PAID_INVOICES_PAYMENTS_PATH.getPath();
        public static final String PAID_INVOICES_PAYMENTS_MANUAL_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + PAID_INVOICES_PAYMENTS_MANUAL_PATH.getPath();
        public static final String AGENTS_UPLOAD_IMAGES_API_PATH =
                getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + AGENTS_UPLOAD_IMAGES_PATH.getPath();
        public static final Function<String, String> PENDING_INVOICE_PAYMENTS_API_PATH =
                (customerEmail) ->
                        getBaseUrl(MVN_PROPERTY_INTEGRATION_COMPONENT_URL) + PENDING_INVOICE_PAYMENTS_PATH
                                .getFilledInPath(customerEmail);
    }

    public static final class EthereumBridge {

        public static final Function<String, String> BALANCE_BY_WALLET_ADDRESS_API_PATH =
                (wallettAddress) ->
                        getBaseUrl(ETHEREUM_BRIDGE_URL)
                                + EthereumBridgeEndpoint.WALLETS_PATH.getPath()
                                + EthereumBridgeEndpoint.BY_WALLET_ADDRESS_PATH.getFilledInPath(wallettAddress);
    }

    public static final class NotificationSystem {

        public static final String TEMPLATES_API_PATH =
                getBaseUrl(NOTIFICATION_SYSTEM_COMPONENT_URL) + TEMPLATES_PATH.getPath();
        public static final String NOTIFICATION_MESSAGE_API_PATH =
                getBaseUrl(NOTIFICATION_SYSTEM_COMPONENT_URL) + NOTIFICATION_MESSAGE_PATH.getPath();
        public static final String NOTIFICATION_MESSAGE_EMAIL_API_PATH =
                NOTIFICATION_MESSAGE_API_PATH + EMAIL_PATH.getPath();
        public static final String NOTIFICATION_MESSAGE_SMS_API_PATH =
                NOTIFICATION_MESSAGE_API_PATH + SMS_PATH.getPath();
        public static final String NOTIFICATION_MESSAGE_PUSH_API_PATH =
                NOTIFICATION_MESSAGE_API_PATH + PUSH_NOTIFICATIONS_PATH.getPath();
    }

    public static final class NotificationSystemAudit {

        public static final String MESSAGE_API_PATH =
                getBaseUrl(NOTIFICATION_SYSTEM_AUDIT_URL) + MESSAGES_ALL.getPath();
        public static final String FAILED_DELIVERY_API_PATH =
                getBaseUrl(NOTIFICATION_SYSTEM_AUDIT_URL) + MESSAGES_FAILED_DELIVERY.getPath();
        public static final String PARSING_ISSUES_API_PATH =
                getBaseUrl(NOTIFICATION_SYSTEM_AUDIT_URL) + MESSAGES_TEMPLATE_PARSING_ISSUES.getPath();
    }

    public static final class OperationsHistory {

        public static final String TRANSACTIONS_API_PATH =
                getBaseUrl(OPERATIONS_HISTORY_URL) + TRANSACTIONS_PATH.getPath();
        public static final Function<String, String> OPERATIONS_BY_CUSTOMER_ID_API_PATH =
                (id) ->
                        TRANSACTIONS_API_PATH + TRANSACTIONS_BY_ID_PATH.getFilledInPath(id);
        public static final String TRANSFERS_API_PATH =
                getBaseUrl(OPERATIONS_HISTORY_URL) + TRANSFERS_PATH.getPath();
        public static final String STATISTICS_PATH =
                getBaseUrl(OPERATIONS_HISTORY_URL) + PathConsts.OperationsHistoryApiEndpoint.STATISTICS_PATH.getPath();
        public static final String STATISTICS_CUSTOMERS_BY_DATE_PATH =
                STATISTICS_PATH + CUSTOMERS_BY_DATE_PATH.getPath();
        public static final String STATISTICS_ACTIVE_CUSTOMERS_PATH =
                STATISTICS_PATH + ACTIVE_CUSTOMERS_PATH.getPath();
        public static final String VOUCHER_PURCHASES_API_PATH =
                getBaseUrl(OPERATIONS_HISTORY_URL) + OperationsHistoryApiEndpoint.VOUCHER_PURCHASES_PATH.getPath();
    }

    public static final class PartnerApi {

        public static final String LOGIN_API_PATH =
                getBaseUrl(PARTNER_API_URL) + LOGIN_PATH.getPath();
        public static final String LOGOUT_API_PATH =
                getBaseUrl(PARTNER_API_URL) + LOGOUT_PATH.getPath();
        public static final String CUSTOMERS_API_PATH =
                getBaseUrl(PARTNER_API_URL) + PartnerApiEndpoint.CUSTOMERS_PATH.getPath();
        @Deprecated
        public static final Function<String, String> CUSTOMER_BALANCE_API_PATH =
                (customerId) ->
                        getBaseUrl(PARTNER_API_URL) + CUSTOMER_BALANCE_PATH.getFilledInPath(customerId);
        public static final String CUSTOMER_BALANCE_QUERY_API_PATH =
                getBaseUrl(PARTNER_API_URL) + PartnerApiEndpoint.QUERY_PATH.getPath();
        public static final String REFERRALS_API_PATH =
                getBaseUrl(PARTNER_API_URL) + PartnerApiEndpoint.REFERRALS_PATH.getPath();
        public static final String BONUS_CUSTOMERS_API_PATH =
                getBaseUrl(PARTNER_API_URL) + PartnerApiEndpoint.BONUS_CUSTOMERS_PATH.getPath();
        public static final String PAYMENTS_API_PATH =
                getBaseUrl(PARTNER_API_URL) + PartnerApiEndpoint.PAYMENTS_PATH.getPath();
        public static final String PAYMENTS_REQUESTS_API_PATH =
                getBaseUrl(PARTNER_API_URL) + PartnerApiEndpoint.PAYMENTS_PATH.getPath()
                        + PartnerApiEndpoint.REQUESTS_PATH.getPath();
        public static final String MESSAGES_API_PATH =
                getBaseUrl(PARTNER_API_URL) + MESSAGES_PATH.getPath();
    }

    public static final class PartnersIntegration {

        public static final String CUSTOMERS_QUERY_API_PATH =
                getBaseUrl(PARTNERS_INTEGRATION_URL) + CUSTOMERS_QUERY_PATH.getPath();
        public static final Function<String, String> CUSTOMER_BALANCE_QUERY_API_PATH =
                (customerId) ->
                        getBaseUrl(PARTNERS_INTEGRATION_URL) + CUSTOMER_BALANCE_QUERY_PATH.getFilledInPath(customerId);
        public static final String REFERRALS_QUERY_API_PATH =
                getBaseUrl(PARTNERS_INTEGRATION_URL) + REFERRALS_QUERY_PATH.getPath();
        public static final String BONUS_CUSTOMERS_API_PATH =
                getBaseUrl(PARTNERS_INTEGRATION_URL) + PartnersIntegrationEndpoint.BONUS_CUSTOMERS_PATH.getPath();
        public static final String REQUESTS_API_PATH =
                getBaseUrl(PARTNERS_INTEGRATION_URL) + PartnersIntegrationEndpoint.PAYMENTS_PATH.getPath()
                        + PartnersIntegrationEndpoint.REQUESTS_PATH.getPath();
        public static final String PAYMENTS_API_PATH =
                getBaseUrl(PARTNERS_INTEGRATION_URL) + PartnersIntegrationEndpoint.PAYMENTS_PATH.getPath();
        public static final String MESSAGES_API_PATH =
                getBaseUrl(PARTNERS_INTEGRATION_URL) + PartnersIntegrationEndpoint.MESSAGES_PATH.getPath();
        public static final Function<String, String> MESSAGE_API_PATH =
                (partnerMessageId) ->
                        getBaseUrl(PARTNERS_INTEGRATION_URL) + String
                                .format(PartnersIntegrationEndpoint.MESSAGE_PATH.getPath(), partnerMessageId);
    }

    public static final class PartnerManagement {

        public static final String LOGIN_API_PATH =
                getBaseUrl(PARTNER_MANAGEMENT_API_URL) + PartnerManagementEndpoint.LOGIN_PATH.getPath();
        public static final String GENERATE_CLIENT_ID_API_PATH =
                getBaseUrl(PARTNER_MANAGEMENT_API_URL) + PartnerManagementEndpoint.GENERATE_CLIENT_ID_PATH.getPath();
        public static final String GENERATE_CLIENT_SECRET_API_PATH =
                getBaseUrl(PARTNER_MANAGEMENT_API_URL) + PartnerManagementEndpoint.GENERATE_CLIENT_SECRET_PATH
                        .getPath();
        public static final String PARTNERS_API_PATH =
                getBaseUrl(PARTNER_MANAGEMENT_API_URL) + PartnerManagementEndpoint.PARTNERS_PATH.getPath();
        public static final Function<String, String> PARTNER_BY_ID_API_PATH =
                (id) ->
                        getBaseUrl(PARTNER_MANAGEMENT_API_URL) + PartnerManagementEndpoint.PARTNERS_PATH.getPath()
                                + PartnerManagementEndpoint.BY_ID_PATH.getFilledInPath(id);
        public static final Function<String, String> PARTNER_BY_CLIENT_ID_API_PATH =
                (clientId) ->
                        getBaseUrl(PARTNER_MANAGEMENT_API_URL) + PartnerManagementEndpoint.PARTNERS_PATH.getPath()
                                + PartnerManagementEndpoint.BY_CLIENT_ID_PATH.getFilledInPath(clientId);
        public static final Function<String, String> PARTNER_BY_LOCATION_ID_API_PATH =
                (locationId) ->
                        getBaseUrl(PARTNER_MANAGEMENT_API_URL) + PartnerManagementEndpoint.PARTNERS_PATH.getPath()
                                + PartnerManagementEndpoint.BY_LOCATION_ID_PATH.getFilledInPath(locationId);
        public static final String PARTNERS_LIST_API_PATH =
                getBaseUrl(PARTNER_MANAGEMENT_API_URL) + PartnerManagementEndpoint.PARTNERS_PATH.getPath()
                        + PartnerManagementEndpoint.LIST_PATH.getPath();
    }

    public static final class PartnersPayments {

        public static final String PAYMENTS_API_PATH =
                getBaseUrl(PARTNERS_PAYMENTS_URL) + PAYMENTS_PATH.getPath();
        public static final String CUSTOMER_PENDING_API_PATH =
                getBaseUrl(PARTNERS_PAYMENTS_URL) + PAYMENTS_PATH.getPath() + CUSTOMER_PENDING_PATH.getPath();
        public static final String CUSTOMER_SUCCEEDED_API_PATH =
                getBaseUrl(PARTNERS_PAYMENTS_URL) + PAYMENTS_PATH.getPath() + CUSTOMER_SUCCEEDED_PATH.getPath();
        public static final String CUSTOMER_FAILED_API_PATH =
                getBaseUrl(PARTNERS_PAYMENTS_URL) + PAYMENTS_PATH.getPath() + CUSTOMER_FAILED_PATH.getPath();
        public static final String CUSTOMER_APPROVAL_API_PATH =
                getBaseUrl(PARTNERS_PAYMENTS_URL) + PAYMENTS_PATH.getPath() + CUSTOMER_APPROVAL_PATH.getPath();
        public static final String CUSTOMER_REJECTION_API_PATH =
                getBaseUrl(PARTNERS_PAYMENTS_URL) + PAYMENTS_PATH.getPath() + CUSTOMER_REJECTION_PATH.getPath();
        public static final String PARTNER_APPROVAL_API_PATH =
                getBaseUrl(PARTNERS_PAYMENTS_URL) + PAYMENTS_PATH.getPath() + PARTNER_APPROVAL_PATH.getPath();
        public static final String PARTNER_REJECTION_API_PATH =
                getBaseUrl(PARTNERS_PAYMENTS_URL) + PAYMENTS_PATH.getPath() + PARTNER_REJECTION_PATH.getPath();
        public static final Function<String, String> PAYMENT_REQUEST_ID_API_PATH =
                (id) ->
                        getBaseUrl(PARTNERS_PAYMENTS_URL) + PAYMENTS_PATH.getPath() + PAYMENT_REQUEST_ID_PATH
                                .getFilledInPath(id);
    }

    public static final class PrivateBlockchainFacade {

        public static final String GENERIC_TRANSFERS_API_PATH =
                getBaseUrl(PRIVATE_BLOCKCHAIN_FACADE_URL) + GENERIC_TRANSFERS_PATH.getPath();
    }

    public static final class PushNotifications {

        public static final String NOTIFICATION_MESSAGES_API_PATH =
                getBaseUrl(PUSH_NOTIFICATIONS) + PushNotificationsEndpoint.NOTIFICATION_MESSAGES_PATH.getPath();
        public static final String NOTIFICATION_MESSAGES_READ_API_PATH =
                getBaseUrl(PUSH_NOTIFICATIONS) + PushNotificationsEndpoint.NOTIFICATION_MESSAGES_PATH.getPath()
                        + PushNotificationsEndpoint.READ_PATH.getPath();
        public static final String NOTIFICATION_MESSAGES_READ_ALL_API_PATH =
                getBaseUrl(PUSH_NOTIFICATIONS) + PushNotificationsEndpoint.NOTIFICATION_MESSAGES_PATH.getPath()
                        + PushNotificationsEndpoint.READ_ALL_PATH.getPath();
        public static final String NOTIFICATION_MESSAGES_UNREAD_COUNT_API_PATH =
                getBaseUrl(PUSH_NOTIFICATIONS) + PushNotificationsEndpoint.NOTIFICATION_MESSAGES_PATH.getPath()
                        + PushNotificationsEndpoint.UNREAD_COUNT_PATH.getPath();
    }

    public static final class QuorumExplorer {

        public static final String BLOCKS_API_PATH =
                getBaseUrl(QUORUM_EXPLORER_URL) + BLOCKS_PATH.getPath();
        public static final Function<Long, String> BLOCK_BY_NUMBER_API_PATH =
                (number) ->
                        getBaseUrl(QUORUM_EXPLORER_URL) + BLOCKS_PATH.getPath() + BY_NUMBER_PATH
                                .getFilledInPath(String.valueOf(number));
        public static final Function<String, String> BLOCK_BY_HASH_API_PATH =
                (hash) ->
                        getBaseUrl(QUORUM_EXPLORER_URL) + BLOCKS_PATH.getPath() + BY_HASH_PATH.getFilledInPath(hash);
        public static final String TRANSACTIONS_API_PATH =
                getBaseUrl(QUORUM_EXPLORER_URL) + QuorumExplorerEndpoint.TRANSACTIONS_PATH.getPath();
        public static final String TRANSACTIONS_BY_BLOCK_NUMBER_API_PATH =
                getBaseUrl(QUORUM_EXPLORER_URL) + BLOCKS_PATH.getPath() + BY_NUMBER_TRANSACTIONS_PATH.getPath();
        public static final String TRANSACTIONS_BY_BLOCK_HASH_API_PATH =
                getBaseUrl(QUORUM_EXPLORER_URL) + BLOCKS_PATH.getPath() + BY_HASH_TRANSACTIONS_PATH.getPath();
        public static final String EVENTS_API_PATH =
                getBaseUrl(QUORUM_EXPLORER_URL) + EVENTS_PATH.getPath();
        public static final String EVENTS_BY_BLOCK_NUMBER_API_PATH =
                getBaseUrl(QUORUM_EXPLORER_URL) + BLOCKS_PATH.getPath() + BY_NUMBER_EVENTS_PATH.getPath();
        public static final String EVENTS_BY_BLOCK_HASH_API_PATH =
                getBaseUrl(QUORUM_EXPLORER_URL) + BLOCKS_PATH.getPath() + BY_HASH_EVENTS_PATH.getPath();
        public static final String TRANSACTION_EVENTS_API_PATH =
                getBaseUrl(QUORUM_EXPLORER_URL) + QuorumExplorerEndpoint.TRANSACTIONS_PATH.getPath()
                        + TRANSACTION_EVENTS_PATH.getPath();
        public static final Function<String, String> TRANSACTION_DETAILS_BY_HASH_API_PATH =
                (hash) ->
                        getBaseUrl(QUORUM_EXPLORER_URL) + QuorumExplorerEndpoint.TRANSACTIONS_PATH.getPath()
                                + TRANSACTION_HASH_PATH.getFilledInPath(hash);

        public static final String MOBILE_BURN_RULES_API_PATH =
                getBaseUrl(CAMPAIGNS_URL) + MOBILE_BURN_RULES_PATH.getPath();

        public static final class InputValidation {

            public static final BiFunction<Long, Long, String> BLOCK_BY_NUMBER_VALIDATION_API_PATH =
                    (number, shift) ->
                            getBaseUrl(QUORUM_EXPLORER_URL) + BLOCKS_PATH.getPath() + BY_NUMBER_PATH
                                    .getFilledInPath(String.valueOf(number + shift));
        }
    }

    public static final class QuorumOperationExecutor {

        public static final Function<String, String> GET_BALANCE_BY_ADDRESS_API_URL =
                (address) ->
                        getBaseUrl(QUORUM_OPERATION_EXECUTOR_URL)
                                + QuorumOperationExecutorEndpoint.ADDRESSES_PATH.getPath()
                                + QuorumOperationExecutorEndpoint.BALANCE_BY_ADDRESS_PATH.getFilledInPath(address);
    }

    public static final class Referral {

        public static final String REFERRAL_API_PATH =
                getBaseUrl(REFERRAL_API_COMPONENT_URL) + REFERRAL_PATH.getPath();
        public static final String REFERRAL_HOTELS_API_PATH =
                getBaseUrl(REFERRAL_API_COMPONENT_URL) + REFERRAL_HOTELS_PATH.getPath();
        public static final String REFERRAL_HOTELS_CONFIRM_API_PATH =
                getBaseUrl(REFERRAL_API_COMPONENT_URL) + REFERRAL_HOTELS_PATH.getPath() + CONFIRM_PATH.getPath();
        public static final String REFERRAL_HOTELS_USE_API_PATH =
                getBaseUrl(REFERRAL_API_COMPONENT_URL) + REFERRAL_HOTELS_PATH.getPath() + USE_PATH.getPath();
        public static final String REFERRAL_HOTELS_BY_REFERRER_ID_API_PATH =
                getBaseUrl(REFERRAL_API_COMPONENT_URL) + REFERRAL_HOTELS_PATH.getPath() + BY_REFERRER_ID_PATH.getPath();
        public static final String REFERRAL_HOTELS_BY_EMAIL_API_PATH =
                getBaseUrl(REFERRAL_API_COMPONENT_URL) + REFERRAL_HOTELS_PATH.getPath() + BY_EMAIL_PATH.getPath();
        public static final String REFERRAL_HOTELS_GET_BY_EMAIL_API_PATH =
                getBaseUrl(REFERRAL_API_COMPONENT_URL) + REFERRAL_HOTELS_PATH.getPath() + GET_BY_EMAIL_PATH.getPath();
        public static final String COMMON_REFERRAL_BY_CUSTOMER_ID_API_PATH =
                getBaseUrl(REFERRAL_API_COMPONENT_URL)
                        + ReferralService.COMMON_REFERRAL_PATH.getPath()
                        + ReferralService.BY_CUSTOMER.getPath();
        public static final String COMMON_REFERRAL_LIST_API_PATH =
                getBaseUrl(REFERRAL_API_COMPONENT_URL)
                        + ReferralService.COMMON_REFERRAL_PATH.getPath()
                        + ReferralService.LIST_PATH.getPath();
    }

    public static final class Reporting {

        public static final String REPORT_API_URL =
                getBaseUrl(REPORTING_URL) + ReportingEndpoint.REPORT_PATH.getPath();
        public static final String REPORT_CSV_API_URL =
                getBaseUrl(REPORTING_URL) + ReportingEndpoint.REPORT_PATH.getPath() + ReportingEndpoint.CSV_PATH
                        .getPath();
    }

    public static final class SmsProviderMock {

        public static final String SEND_SMS_API_PATH =
                getBaseUrl(SMS_PROVIDER_MOCK) + SmsProviderMockEndpoint.SMS_PATH.getPath();
        public static final String SMS_QUERY_PAGINATED_API_PATH =
                SEND_SMS_API_PATH + SMS_QUERY_PATH.getPath();
        public static final Function<String, String> SMS_BY_MESSAGE_ID_API_PATH =
                (messageId) ->
                        SEND_SMS_API_PATH + SmsProviderMockEndpoint.BY_ID_PATH.getFilledInPath(messageId);
        public static final String SENT_SMS_API_PATH =
                getBaseUrl(SMS_PROVIDER_MOCK) + SENT_SMS_PATH.getPath();
        public static final String SENT_SMS_DETAILS_API_PATH =
                SENT_SMS_API_PATH + SMS_DETAILS_PATH.getPath();
    }

    public static final class Tiers {

        public static final Function<String, String> CUSTOMERS_BY_ID_TIER_API_PATH =
                (customerId) ->
                        getBaseUrl(TIERS) + TiersEndpoint.CUSTOMERS_BY_ID_TIER_PATH.getFilledInPath(customerId);
        public static final String REPORTS_NUMBER_OF_CUSTOMERS_PER_TIER_API_PATH =
                getBaseUrl(TIERS) + TiersEndpoint.REPORTS_NUMBER_OF_CUSTOMERS_PER_TIER_PATH.getPath();
        public static final String TIERS_API_PATH =
                getBaseUrl(TIERS) + TiersEndpoint.TIERS_PATH.getPath();
        public static final Function<String, String> TIER_BY_ID_API_PATH =
                (tierId) ->
                        getBaseUrl(TIERS) + TiersEndpoint.TIERS_PATH.getPath() + TiersEndpoint.BY_ID_PATH
                                .getFilledInPath(tierId);
    }

    public static final class TokenStatistics {

        public static final String TOKEN_STATISTICS_JOB_GENERAL_PATH =
                getBaseUrl(TOKENS_STATISTICS_JOB_URL) + GENERAL_PATH.getPath();

        public static final String TOKEN_STATISTICS_JOB_GENERAL_TOKENS_BY_DATA_PATH =
                TOKEN_STATISTICS_JOB_GENERAL_PATH + TOKENS_BY_DATE_PATH.getPath();

        public static final String TOKEN_STATISTICS_JOB_GENERAL_TOKENS_SNAPSHOT_PATH =
                TOKEN_STATISTICS_JOB_GENERAL_PATH + TOKENS_SNAPSHOT_PATH.getPath();

        public static final String TOKEN_STATISTICS_JOB_GENERAL_TOKENS_CURRENT_PATH =
                TOKEN_STATISTICS_JOB_GENERAL_PATH + TOKENS_CURRENT_PATH.getPath();

        public static final String TOKEN_STATISTICS_BY_DAYS_API_PATH =
                TOKEN_STATISTICS_JOB_GENERAL_PATH + BY_DAYS.getPath();
    }

    public static final class Vouchers {

        public static final class Reports {

            public static final String SPEND_RULE_VOUCHERS_API_PATH =
                    getBaseUrl(VOUCHERS_URL) + VouchersEndpoint.REPORTS_PATH.getPath()
                            + VouchersEndpoint.SPEND_RULE_VOUCHERS_PATH.getPath();
        }

        public static final class VouchersCtrl {

            public static final Function<String, String> GET_BY_VOUCHER_ID_API_PATH =
                    (voucherId) ->
                            getBaseUrl(VOUCHERS_URL)
                                    + VouchersEndpoint.VOUCHERS_PATH.getPath()
                                    + VouchersEndpoint.BY_VOUCHER_ID_PATH.getFilledInPath(voucherId);
            public static final Function<String, String> GET_VOUCHERS_BY_SPEND_RULE_ID_API_PATH =
                    (spendRuleId) ->
                            getBaseUrl(VOUCHERS_URL)
                                    + VouchersEndpoint.GET_VOUCHERS_BY_SPEND_RULE_ID_PATH.getFilledInPath(spendRuleId);
            public static final Function<String, String> GET_CUSTOMERS_VOUCHERS_API_PATH =
                    (customerId) ->
                            getBaseUrl(VOUCHERS_URL)
                                    + VouchersEndpoint.GET_CUSTOMERS_VOUCHERS_PATH.getFilledInPath(customerId);
            public static final String VOUCHERS_API_PATH =
                    getBaseUrl(VOUCHERS_URL) + VouchersEndpoint.VOUCHERS_PATH.getPath();
            public static final String CUSTOMERS_API_PATH =
                    getBaseUrl(VOUCHERS_URL) + VouchersEndpoint.CUSTOMERS_PATH.getPath();
        }

    }

    public static final class WalletManagement {

        public static final String BLOCK_WALLET_API_PATH =
                getBaseUrl(WALLET_MANAGEMENT_API_COMPONENT_URL) + WALLET_MNGMT_PATH.getPath() + BLOCK_WALLET_PATH
                        .getPath();
        public static final String UNBLOCK_WALLET_API_PATH =
                getBaseUrl(WALLET_MANAGEMENT_API_COMPONENT_URL) + WALLET_MNGMT_PATH.getPath() + UNBLOCK_WALLET_PATH
                        .getPath();
        public static final Function<String, String> BLOCK_STATUS_BY_ID_API_PATH =
                (id) ->
                        getBaseUrl(WALLET_MANAGEMENT_API_COMPONENT_URL) + WALLET_MNGMT_PATH.getPath() + String
                                .format(BLOCK_STATUS_PATH.getPath(), id);
    }
}
