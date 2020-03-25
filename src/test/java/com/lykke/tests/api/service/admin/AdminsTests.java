package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.JsonConversionUtils.convertFromJsonFile;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.HelperUtils.getTestFilePath;
import static com.lykke.tests.api.common.ResourcesConsts.Admins.AUTOFILL_DATA;
import static com.lykke.tests.api.service.admin.AdminsUtils.createDefaultAdmin;
import static com.lykke.tests.api.service.admin.AdminsUtils.generateAdminPassword;
import static com.lykke.tests.api.service.admin.AdminsUtils.generateSugggestedPassword;
import static com.lykke.tests.api.service.admin.AdminsUtils.getAdmin;
import static com.lykke.tests.api.service.admin.AdminsUtils.getAutofillData;
import static com.lykke.tests.api.service.admin.AdminsUtils.postAdmin;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getTokenForAdminUser;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.api.testing.api.common.PasswordGen;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.AdminListRequest;
import com.lykke.tests.api.service.admin.model.admins.AdminCreateModel;
import com.lykke.tests.api.service.admin.model.admins.AdminListResponse;
import com.lykke.tests.api.service.admin.model.admins.AdminModel;
import com.lykke.tests.api.service.admin.model.admins.GeneratedPasswordModel;
import com.lykke.tests.api.service.admin.model.admins.SuggestedValueMapping;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class AdminsTests extends BaseApiTest {

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2673)
    void shouldCreateAdmin() {
        val password = generateAdminPassword();
        val actualResult = postAdmin(AdminCreateModel
                .adminCreateModelBuilder()
                .email(generateRandomEmail())
                .password(password)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .company(FakerUtils.companyName)
                .department(generateRandomString(10))
                .jobTitle(generateRandomString(10))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminModel.class);

        val token = getTokenForAdminUser(actualResult.getEmail(), password);

        assertAll(
                () -> assertNotNull(token),
                () -> assertEquals(64, token.length())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2673)
    void shouldGenerateAdminPassword() {
        val actualResult = generateSugggestedPassword()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(GeneratedPasswordModel.class);

        assertAll(
                () -> assertNotNull(actualResult.getPassword()),
                () -> assertTrue(PasswordGen.MIN_LENGTH < actualResult.getPassword().length()),
                () -> assertTrue(PasswordGen.MAX_LENGTH > actualResult.getPassword().length())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2673)
    void shouldGetAdmins() {
        val expectedAdmins = Stream.of(createDefaultAdmin(), createDefaultAdmin(), createDefaultAdmin())
                .collect(toList())
                .toArray(new AdminModel[]{});

        val adminList = getAdmin(AdminListRequest
                .builder()
                .searchValue(expectedAdmins[1].getEmail())
                .currentPage(1)
                .pageSize(1)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AdminListResponse.class);

        val expectedAdmin = expectedAdmins[1];
        val actualAdmin = adminList.getItems()[0];

        assertAll(
                () -> assertNotNull(actualAdmin),
                () -> assertEquals(expectedAdmin, actualAdmin)
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2673)
    void shouldCreateAdminWithAutofillData() {
        val jsonFile = getTestFilePath(AUTOFILL_DATA);
        val expectedAutofillData = convertFromJsonFile(jsonFile, SuggestedValueMapping[].class);

        val actualAutofillData = getAutofillData()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SuggestedValueMapping[].class);

        assertAll(
                () -> assertEquals(expectedAutofillData[0].getType(), actualAutofillData[0].getType()),
                () -> assertEquals(Arrays.stream(expectedAutofillData[0].getValues()).sorted().findFirst(),
                        Arrays.stream(actualAutofillData[0].getValues()).sorted().findFirst()),
                () -> assertEquals(expectedAutofillData[1].getType(), actualAutofillData[1].getType()),
                () -> assertEquals(Arrays.stream(expectedAutofillData[1].getValues()).sorted().findFirst(),
                        Arrays.stream(actualAutofillData[1].getValues()).sorted().findFirst())
        );
    }
}
