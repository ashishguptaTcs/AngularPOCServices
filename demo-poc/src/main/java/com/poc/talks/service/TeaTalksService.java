package com.poc.talks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.poc.talks.beans.TeaListBean;
import com.poc.talks.beans.TeaRecipeBean;
import com.poc.talks.exception.DatabaseException;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class TeaTalksService {

    @Autowired
    Environment environment;

    public List getTeaList(String user){
        MongoClient mongoClient = new MongoClient(environment.getProperty("spring.data.mongodb.host"), Integer.valueOf(environment.getProperty("spring.data.mongodb.port")));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(environment.getProperty("mongodb.database"));
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(environment.getProperty("mongodb.tea.collection"));
        List<String> usersToSearch = new ArrayList<>();
        List teaList = new ArrayList<>();
        usersToSearch.add(environment.getProperty("default.User"));

        try{
            Document inQuery = new Document();
            inQuery.put("createdBy", new BasicDBObject("$in", usersToSearch));
            Iterator<Document> itr = mongoCollection.find(inQuery).projection(Projections.excludeId()).iterator();

            while(itr.hasNext()){
                teaList.add(itr.next());
            }

            if(!user.equals(environment.getProperty("default.User"))){

                usersToSearch = new ArrayList<>();
                usersToSearch.add(user);

                inQuery = new Document();
                inQuery.put("createdBy", new BasicDBObject("$in", usersToSearch));
                itr = mongoCollection.find(inQuery).projection(Projections.excludeId()).iterator();
                while (itr.hasNext()) {
                    teaList.add(itr.next());
                }
            }

        }catch(Exception e){
            throw new DatabaseException("Error while reading data from database " + e);
        }finally {
            mongoClient.close();
        }
        return teaList;
    }

    public String addNewTea(TeaRecipeBean teaRecipeBean){
        MongoClient mongoClient = new MongoClient(environment.getProperty("spring.data.mongodb.host"), Integer.valueOf(environment.getProperty("spring.data.mongodb.port")));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(environment.getProperty("mongodb.database"));
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(environment.getProperty("mongodb.tea.collection"));
        ObjectMapper mapper = new ObjectMapper();
        int i = 0;
        StringBuffer response = new StringBuffer();

        try{
            //Fetching number of Recipes already added by User

            List<Document> data = mongoCollection.find(Filters.eq("createdBy", teaRecipeBean.getCreatedBy().trim())).into(
                    new ArrayList<Document>());
            for(Document doc : data){
                i++;
            }

            //Inserting tea recipe into mongoDB
            teaRecipeBean.setId(teaRecipeBean.getCreatedBy() + (i+1));
            String input = mapper.writeValueAsString(teaRecipeBean);
            Document document = Document.parse(input);
            ArrayList<Document> documentList = new ArrayList<>();
            documentList.add(document);
            mongoCollection.insertOne(document);
            response.append("{\n");
            response.append("\"message\" : \"Tea Recipe Added Successfully.\",\n");
            response.append("\"id\" : \"" + (teaRecipeBean.getCreatedBy() + (i+1)) + "\"\n");
            response.append("}");
        }catch(Exception e){
            throw new DatabaseException("Error while performing database operation." + e);
        }finally {
            mongoClient.close();
        }
        return response.toString();
    }

    public TeaRecipeBean getTea(String id, String userId){
        MongoClient mongoClient = new MongoClient(environment.getProperty("spring.data.mongodb.host"), Integer.valueOf(environment.getProperty("spring.data.mongodb.port")));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(environment.getProperty("mongodb.database"));
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(environment.getProperty("mongodb.tea.collection"));
        List teaList = new ArrayList();
        List usersToSearch = new ArrayList();
        usersToSearch.add(environment.getProperty("default.User"));
        usersToSearch.add(userId);
        TeaRecipeBean teaBean = new TeaRecipeBean();
        try{
            List<BasicDBObject> docList = new ArrayList<>();
            Document search = new Document();
            docList.add(new BasicDBObject("id", id));
            docList.add(new BasicDBObject("createdBy", new BasicDBObject("$in", usersToSearch)));
            search.put("$and", docList);
            Iterator<Document> itr = mongoCollection.find(search).projection(Projections.excludeId()).iterator();
            while(itr.hasNext()){
                Document resultDoc = itr.next();
                teaBean.setId(resultDoc.getString("id"));
                teaBean.setBrand(resultDoc.getString("brand"));
                teaBean.setCreatedBy(resultDoc.getString("createdBy"));
                teaBean.setCupSize(resultDoc.getInteger("cupSize"));
                teaBean.setMilk(resultDoc.getBoolean("milk"));
                teaBean.setName(resultDoc.getString("name"));
                teaBean.setSugar(resultDoc.getBoolean("sugar"));
                teaBean.setWater(resultDoc.getBoolean("water"));
                teaBean.setIngredients((List<String>)resultDoc.get("ingredients"));
            }
        }catch (Exception e){
            throw new DatabaseException("Error while reading data from database " + e);
        }finally {
            mongoClient.close();
        }
        return teaBean;
    }

    /*public String addNewTeaToDB(TeaRecipeBean teaRecipeBean){

        MongoClient mongoClient = new MongoClient(environment.getProperty("mongodb.host"), Integer.valueOf(environment.getProperty("mongodb.port")));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(environment.getProperty("mongodb.database"));
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(environment.getProperty("mongodb.tea.collection"));
        ObjectMapper mapper = new ObjectMapper();
        try{
            //Fetching number of Recipes already added by User
            int i = 0;
            List<Document> data = mongoCollection.find(Filters.eq("createdBy", teaRecipeBean.getCreatedBy().trim())).into(
            new ArrayList<Document>());
            for(Document doc : data){
                i++;
            }

            //Inserting tea recipe into mongoDB
            teaRecipeBean.setId(teaRecipeBean.getCreatedBy() + (i+1));
            String input = mapper.writeValueAsString(teaRecipeBean);
            Document document = Document.parse(input);
            ArrayList<Document> documentList = new ArrayList<>();
            documentList.add(document);
            mongoCollection.insertOne(document);

        }catch(Exception e){
            throw new DatabaseException("Error while performing database operation." + e);
        }finally {
            mongoClient.close();
        }
        return "Tea Recipe Added Successfully.";
    }

    public List getTeaListFromDB(String user){
        MongoClient mongoClient = new MongoClient(environment.getProperty("mongodb.host"), Integer.valueOf(environment.getProperty("mongodb.port")));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(environment.getProperty("mongodb.database"));
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(environment.getProperty("mongodb.tea.collection"));
        List<String> usersToSearch = new ArrayList<>();
        List teaList = new ArrayList<>();
        usersToSearch.add(environment.getProperty("default.User"));
        usersToSearch.add(user);
        try{
            Document inQuery = new Document();
            inQuery.put("createdBy", new BasicDBObject("$in", usersToSearch));
            Iterator<Document> itr = mongoCollection.find(inQuery).projection(Projections.excludeId()).iterator();
            while(itr.hasNext()){
                teaList.add(itr.next());
            }

        }catch(Exception e){
            throw new DatabaseException("Error while reading data from database " + e);
        }finally {
            mongoClient.close();
        }
        return teaList;
    }

    public List getTeaFromDB(String id, String userId){
        MongoClient mongoClient = new MongoClient(environment.getProperty("mongodb.host"), Integer.valueOf(environment.getProperty("mongodb.port")));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(environment.getProperty("mongodb.database"));
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(environment.getProperty("mongodb.tea.collection"));
        List teaList = new ArrayList();
        List usersToSearch = new ArrayList();
        usersToSearch.add(environment.getProperty("default.User"));
        usersToSearch.add(userId);
        try{
            List<BasicDBObject> docList = new ArrayList<>();
            Document search = new Document();
            docList.add(new BasicDBObject("id", id));
            docList.add(new BasicDBObject("createdBy", new BasicDBObject("$in", usersToSearch)));
            search.put("$and", docList);
            Iterator<Document> itr = mongoCollection.find(search).projection(Projections.excludeId()).iterator();
            while(itr.hasNext()){
                teaList.add(itr.next());
            }

        }catch (Exception e){
            throw new DatabaseException("Error while reading data from database " + e);
        }finally {
            mongoClient.close();
        }
        return teaList;
    }*/

}
