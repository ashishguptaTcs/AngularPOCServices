package com.poc.user.IMPL;

import com.poc.user.model.User;
import com.poc.user.utill.UserDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDALImpl implements UserDAL {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<User> getAllUsers() {
        return mongoTemplate.findAll(User.class);
    }

    @Override
    public User getUserById(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        return mongoTemplate.findOne(query, User.class);
    }

    @Override
    public User addNewUser(User user) {
        mongoTemplate.save(user);
        return user;
    }

    @Override
    public User authUser(String userId, String applicationId){
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .andOperator(Criteria.where("applicationId").is(applicationId)));
        return mongoTemplate.findOne(query, User.class);
    }

}

