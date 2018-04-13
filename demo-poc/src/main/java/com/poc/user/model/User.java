package com.poc.user.model;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Document
public class User {

    @ApiModelProperty(example = "bucketListApprohan89",
            value = "Id number", hidden = true)
    @Id
    private String id;
    @NotEmpty
    @ApiModelProperty(required = true, dataType = "string",example = "app01")
    private String applicationId;
    private String userName;
    private String name;

    @Size(max = 255)
    @Pattern(regexp = "[^@]+@[^@]+",
            message = "must be valid email address format")
    @ApiModelProperty(value =
            "email address of the user.",
            required = true, example = "test@coolhosting.com")
    private String email;
    @NotEmpty
    @ApiModelProperty(required = true, dataType = "string",example = "Welcome@123")
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }*/
}
