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
import java.util.ArrayList;
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
    @RequestMapping(value = "all/{appId}", method = RequestMethod.GET)
    @CrossOrigin
    public List<User> getAllUsers(@PathVariable String appId) {
        LOG.info("Getting all users.");
        List<User> userList = userRepository.findAll();

        List<User> userList1 = new ArrayList<>();

        for (int index = 0; index < userList.size();index++){
           if(userList.get(index).getApplicationId().trim().equals(appId.trim())){
               userList1.add(userList.get(index));
           }
        }
        //return userRepository.findAll();
        return userList1;
    }

    @RequestMapping(value = "/{applicationID}/{userName}", method = RequestMethod.GET)
    @CrossOrigin
    public User getUser(@PathVariable String applicationID,
                        @PathVariable String userName) {
        LOG.info("Getting user with ID: {}.", userName.trim());
        return userRepository.findOne(applicationID.trim()+userName.trim());
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseEntity<CreateUserResponse> addNewUsers(@RequestBody User user) {

        CreateUserResponse cur = new CreateUserResponse();
        if(user.getApplicationId().trim().equals("bucketListApp")){
            user.setEmail("");
            user.setName("");
        }

        Object obj = null;
        System.out.println("obj - " + obj );
        System.out.println("emp.getUserName() - " + user.getUserName().trim());
        //obj = userRepository.findOne(emp.getUserName());
        obj = userRepository.findOne(user.getApplicationId().trim()+user.getUserName().trim());
        if(obj == null) {
            LOG.info("Saving user.");
            user.setId(user.getApplicationId().trim()+user.getUserName().trim());
            userRepository.save(user);
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
        user  = userRepository.findOne(request.getApplicationId().trim()+request.getUserId().trim());

        if (user!=null) {
            authUserResponse = uService.authenticateUser(user, request);
        }else{
            authUserResponse.setMessage("Unable To Find User");
        }
        return ResponseEntity.ok(authUserResponse);
    }
}

