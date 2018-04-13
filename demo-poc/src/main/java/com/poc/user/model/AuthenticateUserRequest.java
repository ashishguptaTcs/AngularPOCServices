package com.poc.user.model;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class AuthenticateUserRequest {


    private String userId;
    @NotEmpty
    @ApiModelProperty(required = true, dataType = "string",example = "Welcome@123")
    private String password;
    @NotEmpty
    @ApiModelProperty(required = true, dataType = "string",example = "app01")
    private String applicationId;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
