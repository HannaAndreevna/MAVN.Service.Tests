package com.lykke.tests.api.service.mavnpropertyintegration;

import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.AGENTS_CHANGED_SALESMEN_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.HISTORY_APPROVED_LEADS_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.HISTORY_PAID_INVOICES_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.HISTORY_PURCHASES_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.HISTORY_REGISTERED_LEADS_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.INTEGRATION_PAID_INVOICES_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.LEADS_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.MANUAL_AGENTS_CHANGED_SALESMEN_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.MANUAL_APPROVED_LEADS_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.MANUAL_LEADS_CHANGED_SALESMEN_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.MANUAL_PAID_INVOICES_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.MANUAL_PROCESSED_AGENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.MANUAL_PURCHASES_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.MANUAL_REGISTERED_AGENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.MANUAL_REGISTERED_LEADS_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.PAID_INVOICES_PAYMENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.PAID_INVOICES_PAYMENTS_MANUAL_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.PENDING_INVOICE_PAYMENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.PROCESSED_AGENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.REGISTERED_AGENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.MAVNPropertyIntegration.SALESMEN_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.mavnpropertyintegration.model.AgentsChangedSalesmenManualEntryListModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ApprovedLeadsManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetAgentsChangedSalesmenHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetApprovedLeadsHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetPaidInvoicePaymentHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetProcessedAgentsHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetPropertyPurchasesByLeadsRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetRegisteredAgentsHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetRegisteredLeadsHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.HistoryPaidInvoiceRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.IntegrationPaidInvoicesRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.LeadRegisterRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.LeadsChangedSalesmenManualEntryListModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ManualPaidInvoicesRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PaginatedResponseModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PaidInvoicePaymentManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PendingInvoicePaymentsResponseModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ProcessedAgentManualEntryListModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PropertyPurchasesByLeadsManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.RegisterAgentManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.RegisterLeadManualEntryModel;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class MAVNPropertyIntegrationUtils {

    private static final String CUSTOMER_EMAIL_FIELD = "customerEmail";

    // TODO: understand which token should be used
    public Response postLead(LeadRegisterRequestModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(LEADS_API_PATH)
                .thenReturn();
    }

    public Response postManualEntryRegisteredLeads(RegisterLeadManualEntryModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(MANUAL_REGISTERED_LEADS_API_PATH)
                .thenReturn();
    }

    public Response postManualEntryApprovedLeads(ApprovedLeadsManualEntryModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(MANUAL_APPROVED_LEADS_API_PATH)
                .thenReturn();
    }

    public Response postManualEntryPropertyPurchasesByLeads(PropertyPurchasesByLeadsManualEntryModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(MANUAL_PURCHASES_API_PATH)
                .thenReturn();
    }

    public Response postManualEntryLeadsChangedSalesmen(LeadsChangedSalesmenManualEntryListModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(MANUAL_LEADS_CHANGED_SALESMEN_API_PATH)
                .thenReturn();
    }

    public Response postHistoryRegisteredLeads(GetRegisteredLeadsHistoryRequestModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(HISTORY_REGISTERED_LEADS_API_PATH)
                .thenReturn();
    }

    public Response postHistoryApprovedLeads(GetApprovedLeadsHistoryRequestModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(HISTORY_APPROVED_LEADS_API_PATH)
                .thenReturn();
    }

    public Response postHistoryPropertyPurchasesByLeads(GetPropertyPurchasesByLeadsRequestModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(HISTORY_PURCHASES_API_PATH)
                .thenReturn();
    }

    public Response postHistoryRegisteredAgents(GetRegisteredAgentsHistoryRequestModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(REGISTERED_AGENTS_API_PATH)
                .thenReturn();
    }

    public Response postHistoryProcessesAgents(GetProcessedAgentsHistoryRequestModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(PROCESSED_AGENTS_API_PATH)
                .thenReturn();
    }

    public Response postManualEntryAgentsChangedSalesmen(AgentsChangedSalesmenManualEntryListModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(MANUAL_AGENTS_CHANGED_SALESMEN_API_PATH)
                .thenReturn();
    }

    public Response getSalesmanBySalesforceId(String salesforceId) {
        return getHeader(getAdminToken())
                .get(SALESMEN_API_PATH.apply(salesforceId))
                .thenReturn();
    }

    public Stream<Map<String, String>> postHistoryAgentsChangedSalesman(
            GetAgentsChangedSalesmenHistoryRequestModel requestObject) {
        val temporaryCollection = getHeader(getAdminToken())
                .body(requestObject)
                .post(AGENTS_CHANGED_SALESMEN_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedResponseModel.class)
                .getData();
        return convertCollectionToStream(temporaryCollection);
    }

    public Stream<Map<String, String>> getRegisteredAgentHistory(
            GetRegisteredAgentsHistoryRequestModel requestObject) {
        val temporaryCollection =
                postHistoryRegisteredAgents(requestObject)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PaginatedResponseModel.class)
                        .getData();
        return convertCollectionToStream(temporaryCollection);
    }

    public Stream<Map<String, String>> getProcessesAgentHistory(
            GetProcessedAgentsHistoryRequestModel requestObject) {
        val temporaryCollection =
                postHistoryProcessesAgents(requestObject)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(PaginatedResponseModel.class)
                        .getData();
        return convertCollectionToStream(temporaryCollection);
    }

    public Response postManualEntryRegisteredAgents(RegisterAgentManualEntryModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(MANUAL_REGISTERED_AGENTS_API_PATH)
                .thenReturn();
    }

    public Response postManualEntryProcessedAgents(ProcessedAgentManualEntryListModel requestObject) {

        return getHeader(getAdminToken())
                .body(requestObject)
                .post(MANUAL_PROCESSED_AGENTS_API_PATH)
                .thenReturn();
    }

    public Response postManualEntryPaidInvoices(ManualPaidInvoicesRequestModel requestObject) {
        return getHeader()
                .body(requestObject)
                .post(MANUAL_PAID_INVOICES_API_PATH)
                .thenReturn();
    }

    public Response postIntegrationPaidInvoices(IntegrationPaidInvoicesRequestModel requestObject) {
        return getHeader()
                .body(requestObject)
                .post(INTEGRATION_PAID_INVOICES_API_PATH)
                .thenReturn();
    }

    public Response postHistoryPaidInvoices(HistoryPaidInvoiceRequestModel requestObject) {
        return getHeader()
                .body(requestObject)
                .post(HISTORY_PAID_INVOICES_API_PATH)
                .thenReturn();
    }

    public Response getPaidInvoicePayments(GetPaidInvoicePaymentHistoryRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(PAID_INVOICES_PAYMENTS_API_PATH)
                .thenReturn();
    }

    public Response postPaidInvoicePayments(PaidInvoicePaymentManualEntryModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(PAID_INVOICES_PAYMENTS_MANUAL_API_PATH)
                .thenReturn();
    }

    public PendingInvoicePaymentsResponseModel[] getPendingInvoicePayments(String customerEmail) {
        return getHeader()
                .get(PENDING_INVOICE_PAYMENTS_API_PATH.apply(customerEmail))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PendingInvoicePaymentsResponseModel[].class);
    }

    private Stream<Map<String, String>> convertCollectionToStream(Object[] temporaryCollection) {
        if (null == temporaryCollection || 0 == temporaryCollection.length) {
            return Stream.of(new LinkedHashMap<>());
        }
        val convertedCollection = Arrays.stream(temporaryCollection)
                .map(item -> (Map<String, String>) item);
        return convertedCollection;
    }
}
