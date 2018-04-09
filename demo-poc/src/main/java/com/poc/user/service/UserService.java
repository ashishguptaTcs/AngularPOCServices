package com.poc.user.service;

import com.poc.user.model.AuthenticateUserRequest;
import com.poc.user.model.AuthenticateUserResponse;
import com.poc.user.model.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class UserService {


    public AuthenticateUserResponse authenticateUser(User user,AuthenticateUserRequest request) {
        AuthenticateUserResponse response = new AuthenticateUserResponse();

        String userId = user.getUserName();
        String password = user.getPassword();

        String inPass = request.getPassword();
        String inUserId = request.getUserId();

        if(userId.equals(inUserId)  && password.equals(inPass)){
            response.setMessage("User Authenticate");
        }
        if(!password.equals(inPass)){
            response.setMessage("Wrong Password");
        }
        return response;
    }
}
