package com.poc.user.utill;

import com.poc.user.model.User;

import java.util.List;

public interface UserDAL {

    List<User> getAllUsers();

    User getUserById(String userId);

    User addNewUser(User user);

    User authUser(String userId,String applicationId);

}
