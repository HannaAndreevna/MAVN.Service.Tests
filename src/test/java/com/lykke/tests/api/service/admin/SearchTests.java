package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.SearchUtils.postAdminsSearch;
import static com.lykke.tests.api.service.admin.SearchUtils.postCustomersSearch;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.loginAdmin;
import static com.lykke.tests.api.service.adminmanagement.RegisterAdminUtils.registerAdmin;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.AdminListRequest;
import com.lykke.tests.api.service.admin.model.CustomerAgentStatus;
import com.lykke.tests.api.service.admin.model.admins.AdminListResponse;
import com.lykke.tests.api.service.admin.model.admins.AdminModel;
import com.lykke.tests.api.service.admin.model.CustomerActivityStatus;
import com.lykke.tests.api.service.admin.model.CustomerListRequest;
import com.lykke.tests.api.service.admin.model.CustomerListResponse;
import com.lykke.tests.api.service.admin.model.CustomerModel;
import com.lykke.tests.api.service.admin.model.common.PagedResponseModel;
import com.lykke.tests.api.service.adminmanagement.model.AuthenticateRequestModel;
import com.lykke.tests.api.service.adminmanagement.model.RegistrationRequestModel;
import com.lykke.tests.api.service.adminmanagement.model.RegistrationResponseModel;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class SearchTests extends BaseApiTest {

    private static final int PAGE_SIZE = 500;
    private static final int TOTAL_COUNT = 1;
    private static final int CURRENT_PAGE = 1;
    private RegistrationRequestModel adminInputData;
    private RegistrationResponseModel adminData;
    private String adminToken;

    @BeforeEach
    void setUp() {
        adminInputData = RegistrationRequestModel
                .builder()
                .email(generateRandomEmail())
                .password(generateValidPassword())
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .company(FakerUtils.companyName)
                .jobTitle(generateRandomString(10))
                .department(generateRandomString(10))
                .phoneNumber(FakerUtils.phoneNumber)
                .build();
        adminData = registerAdmin(adminInputData);
        adminToken = loginAdmin(AuthenticateRequestModel
                .builder()
                .email(adminInputData.getEmail())
                .password(adminInputData.getPassword())
                .build())
                .getToken();
    }

    ////xx
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3337)
    void shouldGetCustomersList() {
        val customerData = registerDefaultVerifiedCustomer();
        val expectedResult = CustomerListResponse
                .builder()
                .pagedResponse(PagedResponseModel
                        .builder()
                        .currentPage(CURRENT_PAGE)
                        .totalCount(TOTAL_COUNT)
                        .build())
                .customers(new CustomerModel[]{
                        CustomerModel
                                .builder()
                                .customerId(customerData.getCustomerId())
                                .email(customerData.getEmail())
                                .firstName(customerData.getFirstName())
                                .lastName(customerData.getLastName())
                                .customerStatus(CustomerActivityStatus.ACTIVE)
                                .phoneNumber(customerData.getPhoneNumber())
                                .customerAgentStatus(CustomerAgentStatus.NOT_AGENT)
                                .build()

                })
                .build();

        val actualResult = postCustomersSearch(CustomerListRequest
                .builder()
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .searchValue(customerData.getEmail())
                .build(), adminToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerListResponse.class);

        assertEquals(expectedResult, actualResult);

        /*
        expected: <CustomerListResponse(pagedResponse=PagedResponseModel(currentPage=1, totalCount=1), customers=[CustomerModel(customerId=c8e02116-568f-4f3d-b0b6-fef7c14a527f, email=jdcccm4rjokqr67pqiporhpbcil2xbqse1jigvvqgodfbquouyqnugm8hjvtixl@example.com, isEmailVerified=false, phoneNumber=null, isPhoneVerified=false, firstName=Niki, lastName=Miller, registeredDate=null, referralCode=null, customerStatus=ACTIVE, customerAgentStatus=null)])>
         but was: <CustomerListResponse(pagedResponse=PagedResponseModel(currentPage=1, totalCount=1), customers=[CustomerModel(customerId=c8e02116-568f-4f3d-b0b6-fef7c14a527f, email=jdcccm4rjokqr67pqiporhpbcil2xbqse1jigvvqgodfbquouyqnugm8hjvtixl@example.com, isEmailVerified=false, phoneNumber=+916558260227, isPhoneVerified=false, firstName=Niki, lastName=Miller, registeredDate=Fri Nov 22 02:04:19 MSK 2019, referralCode=null, customerStatus=ACTIVE, customerAgentStatus=NOT_AGENT)])>
        */
    }

    ////xx
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3337)
    void shouldGetAdminsList() {
        val expectedResult = AdminListResponse
                .builder()
                .pagedResponse(PagedResponseModel
                        .builder()
                        .currentPage(CURRENT_PAGE)
                        .totalCount(TOTAL_COUNT)
                        .build())
                .items(new AdminModel[]{
                        AdminModel
                                .builder()
                                .id(adminData.getAdmin().getAdminUserId())
                                .email(adminInputData.getEmail())
                                .firstName(adminInputData.getFirstName())
                                .lastName(adminInputData.getLastName())
                                .company(adminInputData.getCompany())
                                .department(adminInputData.getDepartment())
                                .jobTitle(adminInputData.getJobTitle())
                                .phoneNumber(adminInputData.getPhoneNumber())
                                .build()
                })
                .build();

        val actualResult = postAdminsSearch(AdminListRequest
                .builder()
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .searchValue(adminInputData.getEmail())
                .build(), adminToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminListResponse.class);

        assertEquals(expectedResult, actualResult);

        /*
        expected: <AdminListResponse(pagedResponse=PagedResponseModel(currentPage=1, totalCount=1), items=[AdminModel(id=071100a8-98b7-4244-9bc7-1a7a7b64278e, email=ovy1hxnkueafpirsg7k5sv0gj3guvm0fmflx0xuimccmfs45gawlfn9g9gwwtla@example.com, firstName=Niki, lastName=Miller, registered=null, phoneNumber=null, company=null, department=null, jobTitle=null)])>
         but was: <AdminListResponse(pagedResponse=PagedResponseModel(currentPage=1, totalCount=1), items=[AdminModel(id=071100a8-98b7-4244-9bc7-1a7a7b64278e, email=ovy1hxnkueafpirsg7k5sv0gj3guvm0fmflx0xuimccmfs45gawlfn9g9gwwtla@example.com, firstName=Niki, lastName=Miller, registered=Fri Nov 22 02:04:18 MSK 2019, phoneNumber=509-177-6701, company=Weissnat LLC, department=WldOUIxa2E, jobTitle=CcLx44akF8)])>
        */
    }
}
