package com.poc.user.controller;


import com.poc.user.model.AuthenticateUserRequest;
import com.poc.user.model.AuthenticateUserResponse;
import com.poc.user.model.CreateUserResponse;
import com.poc.user.model.User;
import com.poc.user.repository.UserRepository;
import com.poc.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import java.util.List;

@RestController
@RequestMapping(value = "/user" , produces = "application/json")
@CrossOrigin
public class UserController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;
    private UserService uService;

    public UserController(UserRepository userRepository,
                          UserService uService) {
        this.userRepository = userRepository;
        this.uService = uService;
    }


    @ApiOperation(value = "User List")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @CrossOrigin
    public List<User> getAllUsers() {
        LOG.info("Getting all users.");
        return userRepository.findAll();
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.GET)
    @CrossOrigin
    public User getUser(@PathVariable String userName) {
        LOG.info("Getting user with ID: {}.", userName);
        return userRepository.findOne(userName);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseEntity<CreateUserResponse> addNewUsers(@RequestBody User emp) {

        Object obj = null;
        CreateUserResponse cur = new CreateUserResponse();
        System.out.println("obj - " + obj );
        System.out.println("emp.getUserName() - " + emp.getUserName());
        obj = userRepository.findOne(emp.getUserName());
        if(obj == null) {
            LOG.info("Saving user.");
            userRepository.save(emp);
            cur.setMessage("User Created Successfully");
            return ResponseEntity.ok(cur);
        }
        cur.setMessage("User Id Already Exist");
        return ResponseEntity.ok(cur);
    }

    @RequestMapping(value = "/authenticate/user", method = RequestMethod.PUT)
    @CrossOrigin
    public ResponseEntity<AuthenticateUserResponse> authUserUsers(@RequestBody AuthenticateUserRequest request) {
        AuthenticateUserResponse authUserResponse = new AuthenticateUserResponse();
        User user = null;
        user  = userRepository.findOne(request.getUserId());

        if (user!=null) {
            authUserResponse = uService.authenticateUser(user, request);
        }else{
            authUserResponse.setMessage("Unable To Find User");
        }
        return ResponseEntity.ok(authUserResponse);
    }






}

