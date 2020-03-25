package com.lykke.tests.api.common;

import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.Urls.AdminApi.VerbAction.DELETE;
import static com.lykke.tests.api.common.Urls.AdminApi.VerbAction.GET;
import static com.lykke.tests.api.common.Urls.AdminApi.VerbAction.POST;
import static com.lykke.tests.api.common.Urls.AdminApi.VerbAction.PUT;
import static com.lykke.tests.api.service.admin.model.permissions.PermissionType.ACTION_RULES;
import static com.lykke.tests.api.service.admin.model.permissions.PermissionType.ADMIN_USERS;
import static com.lykke.tests.api.service.admin.model.permissions.PermissionType.BLOCKCHAIN_OPERATIONS;
import static com.lykke.tests.api.service.admin.model.permissions.PermissionType.CUSTOMERS;
import static com.lykke.tests.api.service.admin.model.permissions.PermissionType.DASHBOARD;
import static com.lykke.tests.api.service.admin.model.permissions.PermissionType.PROGRAM_PARTNERS;
import static com.lykke.tests.api.service.admin.model.permissions.PermissionType.SETTINGS;
import static com.lykke.tests.api.service.adminmanagement.model.AdminPermissionLevel.EDIT;
import static com.lykke.tests.api.service.adminmanagement.model.AdminPermissionLevel.VIEW;

import com.lykke.tests.api.service.admin.model.permissions.PermissionType;
import com.lykke.tests.api.service.adminmanagement.model.AdminPermissionLevel;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import io.restassured.specification.RequestSenderOptions;
import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Urls {

    public static class AdminApi {

        public enum VerbAction {
            GET((x, url) -> x.get(String.format(url, getRandomUuid(), getRandomUuid())).thenReturn()),
            POST((x, url) -> x.post(String.format(url, getRandomUuid(), getRandomUuid())).thenReturn()),
            PUT((x, url) -> x.put(String.format(url, getRandomUuid(), getRandomUuid())).thenReturn()),
            DELETE((x, url) -> x.delete(String.format(url, getRandomUuid(), getRandomUuid())).thenReturn());

            @Getter
            private BiFunction<RequestSenderOptions, String, ResponseOptions<Response>> verbAction;

            VerbAction(BiFunction<RequestSenderOptions, String, ResponseOptions<Response>> action) {
                this.verbAction = action;
            }
        }

        @AllArgsConstructor
        public enum Admins {
            a(GET, "/api/Admins", ADMIN_USERS, VIEW),
            b(PUT, "/api/Admins", ADMIN_USERS, EDIT),
            c(POST, "/api/Admins", ADMIN_USERS, EDIT),
            d(PUT, "/api/Admins/permissions/%s", ADMIN_USERS, EDIT),
            e(GET, "/api/Admins/generateSuggestedPassword", ADMIN_USERS, VIEW),
            f(GET, "/api/Admins/autofillData", ADMIN_USERS, VIEW),
            g(POST, "/api/Admins/search", ADMIN_USERS, VIEW),
            h(GET, "/api/Admins/permissions", ADMIN_USERS, VIEW);

            @Getter
            private VerbAction verbAction;
            @Getter
            private String path;
            @Getter
            private PermissionType permissionType;
            @Getter
            private AdminPermissionLevel adminPermissionLevel;
        }

        /*
        // there're no permissions
        @AllArgsConstructor
        public enum Auth {
            a(POST, "/api/auth/login"),
            b(POST, "/api/auth/changePassword"),
            c(POST, "/api/auth/changePasswordAnonymous"),
            d(POST, "/api/auth/decline-logout"),
            e(POST, "/api/auth/logout");

            @Getter
            private VerbAction verbAction;
            @Getter
            private String path;
            @Getter
            private PermissionType permissionType;
            @Getter
            private AdminPermissionLevel adminPermissionLevel;
        }
        */

        @AllArgsConstructor
        public enum Blockchain {
            a(GET, "/api/blocks", BLOCKCHAIN_OPERATIONS, VIEW),
            b(GET, "/api/blocks/byNumber/%s", BLOCKCHAIN_OPERATIONS, VIEW),
            c(GET, "/api/blocks/byNumber", BLOCKCHAIN_OPERATIONS, VIEW),
            d(GET, "/api/blocks/byNumber/%s/transactions", BLOCKCHAIN_OPERATIONS, VIEW),
            e(GET, "/api/blocks/byNumber/transactions", BLOCKCHAIN_OPERATIONS, VIEW),
            f(GET, "/api/blocks/byNumber/%s/events", BLOCKCHAIN_OPERATIONS, VIEW),
            g(GET, "/api/blocks/byNumber/events", BLOCKCHAIN_OPERATIONS, VIEW),
            h(GET, "/api/blocks/byHash/%s", BLOCKCHAIN_OPERATIONS, VIEW),
            i(GET, "/api/blocks/byHash", BLOCKCHAIN_OPERATIONS, VIEW),
            j(GET, "/api/blocks/byHash/%s/transactions", BLOCKCHAIN_OPERATIONS, VIEW),
            k(GET, "/api/blocks/byHash/transactions", BLOCKCHAIN_OPERATIONS, VIEW),
            l(GET, "/api/blocks/byHash/%s/events", BLOCKCHAIN_OPERATIONS, VIEW),
            m(GET, "/api/blocks/byHash/events", BLOCKCHAIN_OPERATIONS, VIEW),
            n(GET, "/api/transactions", BLOCKCHAIN_OPERATIONS, VIEW),
            o(GET, "/api/transactions/%s", BLOCKCHAIN_OPERATIONS, VIEW),
            p(GET, "/api/transactions/hash", BLOCKCHAIN_OPERATIONS, VIEW),
            q(GET, "/api/transactions/%s/events", BLOCKCHAIN_OPERATIONS, VIEW),
            r(GET, "/api/transactions/events", BLOCKCHAIN_OPERATIONS, VIEW),
            s(GET, "/api/events", BLOCKCHAIN_OPERATIONS, VIEW);

            @Getter
            private VerbAction verbAction;
            @Getter
            private String path;
            @Getter
            private PermissionType permissionType;
            @Getter
            private AdminPermissionLevel adminPermissionLevel;
        }

        @AllArgsConstructor
        public enum BonusTypes {
            a(GET, "/api/BonusTypes", ACTION_RULES, VIEW);

            @Getter
            private VerbAction verbAction;
            @Getter
            private String path;
            @Getter
            private PermissionType permissionType;
            @Getter
            private AdminPermissionLevel adminPermissionLevel;
        }

        @AllArgsConstructor
        public enum BurnRules {
            a(GET, "/api/BurnRules", ACTION_RULES, VIEW),
            b(PUT, "/api/BurnRules", ACTION_RULES, EDIT),
            c(POST, "/api/BurnRules", ACTION_RULES, EDIT),
            d(DELETE, "/api/BurnRules", ACTION_RULES, EDIT),
            e(GET, "/api/BurnRules/%s", ACTION_RULES, VIEW),
            f(DELETE, "/api/BurnRules/%s", ACTION_RULES, EDIT),
            g(GET, "/api/BurnRules/query", ACTION_RULES, VIEW),
            // ??
            h(POST, "/api/BurnRules/vouchers", ACTION_RULES, VIEW),
            i(PUT, "/api/BurnRules/image", ACTION_RULES, EDIT),
            j(POST, "/api/BurnRules/image", ACTION_RULES, EDIT);

            @Getter
            private VerbAction verbAction;
            @Getter
            private String path;
            @Getter
            private PermissionType permissionType;
            @Getter
            private AdminPermissionLevel adminPermissionLevel;
        }

        @AllArgsConstructor
        public enum Customers {
            a(POST, "/api/Customers/search", CUSTOMERS, VIEW),
            b(GET, "/api/Customers/history", CUSTOMERS, VIEW),
            c(GET, "/api/Customers/%s", CUSTOMERS, VIEW),
            d(GET, "/api/Customers/query", CUSTOMERS, VIEW),
            e(GET, "/api/Customers/%s/balance", CUSTOMERS, VIEW),
            f(GET, "/api/Customers/balance", CUSTOMERS, VIEW),
            g(GET, "/api/Customers/%s/walletAddress", CUSTOMERS, VIEW),
            h(GET, "/api/Customers/walletAddress", CUSTOMERS, VIEW),
            i(GET, "/api/Customers/%s/publicWalletAddress", CUSTOMERS, VIEW),
            j(GET, "/api/Customers/publicWalletAddress", CUSTOMERS, VIEW),
            k(POST, "/api/Customers/block/%s", CUSTOMERS, EDIT),
            l(POST, "/api/Customers/block", CUSTOMERS, EDIT),
            m(POST, "/api/Customers/unblock/%s", CUSTOMERS, EDIT),
            n(POST, "/api/Customers/unblock", CUSTOMERS, EDIT),
            o(POST, "/api/Customers/blockWallet/%s", CUSTOMERS, EDIT),
            p(POST, "/api/Customers/blockWallet", CUSTOMERS, EDIT),
            q(POST, "/api/Customers/unblockWallet/%s", CUSTOMERS, EDIT),
            r(POST, "/api/Customers/unblockWallet", CUSTOMERS, EDIT);

            @Getter
            private VerbAction verbAction;
            @Getter
            private String path;
            @Getter
            private PermissionType permissionType;
            @Getter
            private AdminPermissionLevel adminPermissionLevel;
        }

        @AllArgsConstructor
        public enum Dashboard {
            a(GET, "/api/Dashboard/leads", DASHBOARD, VIEW),
            b(GET, "/api/Dashboard/customers", DASHBOARD, VIEW),
            c(GET, "/api/Dashboard/tokens", DASHBOARD, VIEW);

            @Getter
            private VerbAction verbAction;
            @Getter
            private String path;
            @Getter
            private PermissionType permissionType;
            @Getter
            private AdminPermissionLevel adminPermissionLevel;
        }

        @AllArgsConstructor
        public enum EarnRules {
            a(GET, "/api/EarnRules", ACTION_RULES, VIEW),
            b(PUT, "/api/EarnRules", ACTION_RULES, EDIT),
            c(POST, "/api/EarnRules", ACTION_RULES, EDIT),
            d(DELETE, "/api/EarnRules", ACTION_RULES, EDIT),
            e(GET, "/api/EarnRules/%s", ACTION_RULES, VIEW),
            f(GET, "/api/EarnRules/query", ACTION_RULES, VIEW),
            g(PUT, "/api/EarnRules/image", ACTION_RULES, EDIT),
            h(POST, "/api/EarnRules/image", ACTION_RULES, EDIT),
            i(DELETE, "/api/EarnRules/%s", ACTION_RULES, EDIT);

            @Getter
            private VerbAction verbAction;
            @Getter
            private String path;
            @Getter
            private PermissionType permissionType;
            @Getter
            private AdminPermissionLevel adminPermissionLevel;
        }

        /*
        // amonymous
        @AllArgsConstructor
        public enum Home {
                    (GET,"/");
        }
        */

        /*
        // amonymous
        @AllArgsConstructor
        public enum IsAlive {
                    (GET,"/api/IsAlive");
        }
        */

        @AllArgsConstructor
        public enum Partners {
            a(GET, "/api/Partners", PROGRAM_PARTNERS, VIEW),
            b(PUT, "/api/Partners", PROGRAM_PARTNERS, EDIT),
            c(POST, "/api/Partners", PROGRAM_PARTNERS, EDIT),
            d(GET, "/api/Partners/%s", PROGRAM_PARTNERS, VIEW),
            e(GET, "/api/Partners/query", PROGRAM_PARTNERS, VIEW),
            f(POST, "/api/Partners/generateClientSecret", PROGRAM_PARTNERS, VIEW),
            g(POST, "/api/Partners/generateClientId", PROGRAM_PARTNERS, VIEW);

            @Getter
            private VerbAction verbAction;
            @Getter
            private String path;
            @Getter
            private PermissionType permissionType;
            @Getter
            private AdminPermissionLevel adminPermissionLevel;
        }

        @AllArgsConstructor
        public enum Reports {
            a(POST, "/api/Reports", PermissionType.REPORTS, VIEW),
            b(GET, "/api/Reports/exportToCsv", PermissionType.REPORTS, VIEW);

            @Getter
            private VerbAction verbAction;
            @Getter
            private String path;
            @Getter
            private PermissionType permissionType;
            @Getter
            private AdminPermissionLevel adminPermissionLevel;
        }

        @AllArgsConstructor
        public enum Settings {
            a(GET, "/api/Settings/globalCurrencyRate", SETTINGS, VIEW),
            b(PUT, "/api/Settings/globalCurrencyRate", SETTINGS, EDIT),
            c(GET, "/api/Settings/agentRequirements", SETTINGS, VIEW),
            d(PUT, "/api/Settings/agentRequirements", SETTINGS, EDIT),
            e(GET, "/api/Settings/operationFees", SETTINGS, VIEW),
            f(PUT, "/api/Settings/operationFees", SETTINGS, EDIT);


            @Getter
            private VerbAction verbAction;
            @Getter
            private String path;
            @Getter
            private PermissionType permissionType;
            @Getter
            private AdminPermissionLevel adminPermissionLevel;
        }

        @AllArgsConstructor
        public enum Statistics {
            a(GET, "/api/Statistics/customers", DASHBOARD, VIEW),
            b(GET, "/api/Statistics/tokens-current", DASHBOARD, VIEW),
            c(GET, "/api/Statistics/leads", DASHBOARD, VIEW);
            // there're no permissions
            // d(GET, "/api/Statistics/total-supply");

            @Getter
            private VerbAction verbAction;
            @Getter
            private String path;
            @Getter
            private PermissionType permissionType;
            @Getter
            private AdminPermissionLevel adminPermissionLevel;
        }
    }
}
