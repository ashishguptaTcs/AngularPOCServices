package com.poc.talks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.poc.talks.beans.UserDetails;
import com.poc.talks.exception.DatabaseException;
import com.poc.talks.exception.InvalidCredentialsException;
import com.poc.talks.exception.UserAlreadyExistsException;
import com.poc.talks.exception.UserNotFoundException;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Service
public class LoginService {

    @Autowired
    Environment environment;

    public UserDetails logging(UserDetails user)
    {
        UserDetails userBean = new UserDetails();
        String response = "";
        Boolean result = checkUser(user.getUserId());
        if (result) {
            MongoClient mongoClient = new MongoClient(environment.getProperty("spring.data.mongodb.host"), Integer.valueOf(environment.getProperty("spring.data.mongodb.port")));
            MongoDatabase mongoDatabase = mongoClient.getDatabase(environment.getProperty("mongodb.database"));
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(environment.getProperty("mongodb.tea.users.collection"));
            try{
                Document search = new Document();
                search.put("userId", user.getUserId().toUpperCase());
                Iterator<Document> itr = mongoCollection.find(search).projection(Projections.excludeId()).iterator();
                while(itr.hasNext())
                {
                    Document resultDoc = new Document();
                    resultDoc = itr.next();
                    userBean.setPassword(resultDoc.get("password").toString());
                    userBean.setUserId(user.getUserId());
                    userBean.setFirstName(resultDoc.get("firstName").toString());
                    userBean.setLastName(resultDoc.get("lastName").toString());

                }
            }
            catch (Exception e)
            {
                throw new DatabaseException("Error while performing database operation." + e);
            }
            finally
            {
                mongoClient.close();
            }

            if(userBean.getPassword().equals(user.getPassword()))
            {
                userBean.setPassword("");
                return userBean;
            }
            else
            {
                throw new InvalidCredentialsException("Invalid Credentials against id :" +user.getUserId());
            }

        }else
            throw new UserNotFoundException("No User present against User id :" +user.getUserId());


    }


    public String signup(UserDetails user)
    {
        Boolean flag=checkUser(user.getUserId());

        if(!flag) {

            MongoClient mongoClient = new MongoClient(environment.getProperty("spring.data.mongodb.host"), Integer.valueOf(environment.getProperty("spring.data.mongodb.port")));
            MongoDatabase mongoDatabase = mongoClient.getDatabase(environment.getProperty("mongodb.database"));
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(environment.getProperty("mongodb.tea.users.collection"));
            ObjectMapper mapper = new ObjectMapper();
            try {
                user.setUserId(user.getUserId().toUpperCase());
                String input = mapper.writeValueAsString(user);
                Document document = Document.parse(input);
                ArrayList<Document> documentList = new ArrayList<>();
                documentList.add(document);
                mongoCollection.insertOne(document);

            } catch (Exception e) {
                throw new DatabaseException("Error while performing database operation." + e);

            }
            finally
            {
                mongoClient.close();
            }
        }
        else{
            throw new UserAlreadyExistsException("User already Exist with User Id :" +user.getUserId());
        }
        return "User Added Successfully";
    }

    public Boolean checkUser(String userId)
    {

        Boolean flag=false;
        MongoClient mongoClient = new MongoClient(environment.getProperty("spring.data.mongodb.host"), Integer.valueOf(environment.getProperty("spring.data.mongodb.port")));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(environment.getProperty("mongodb.database"));
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(environment.getProperty("mongodb.tea.users.collection"));
        try{
            Document search = new Document();
            search.put("userId", userId.toUpperCase());
            Iterator<Document> itr = mongoCollection.find(search).projection(Projections.excludeId()).iterator();
            if(itr.hasNext())
            {
                flag=true;
            }
        }catch (Exception e){
            throw new DatabaseException("Error while reading data from database " + e);
        }finally {
            mongoClient.close();
        }

        return flag;
    }

}
