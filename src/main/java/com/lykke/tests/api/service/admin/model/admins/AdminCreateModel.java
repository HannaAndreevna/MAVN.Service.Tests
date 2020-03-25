package com.lykke.tests.api.service.admin.model.admins;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
public class AdminCreateModel extends AdminEditBaseModel {

    private String email;
    private String password;

    @Builder(builderMethodName = "adminCreateModelBuilder")
    public AdminCreateModel(String firstName, String lastName, String phoneNumber, String company, String department,
            String jobTitle, String email, String password) {
        super(firstName, lastName, phoneNumber, company, department, jobTitle);
        this.email = email;
        this.password = password;
    }
}
