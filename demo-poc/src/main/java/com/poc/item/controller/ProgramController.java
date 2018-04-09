package com.poc.item.controller;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.poc.item.bean.ItemDto;
import com.mongodb.client.model.Filters;

import java.util.*;

import com.mongodb.util.JSON;
import com.poc.item.bean.ZipBean;
import org.bson.conversions.Bson;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import org.slf4j.Logger;



@RestController
public class ProgramController{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @RequestMapping(value ="/details", method = RequestMethod.GET)
    @CrossOrigin
    public String getDetails(){

        LOG.info("Entering into getDetails()");
        // Retrieving a collection
        MongoCollection<Document> collection = getCollection("mongoDb","productCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        List<Document> dataList = (List<Document>) collection.find().into(
                new ArrayList<Document>());

        if(dataList.size()==0){
            String message="{'message':'No result found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        StringBuffer details = new StringBuffer();
        details.append("[{");

        int i = 0;
        for (Document items : dataList) {
            String id = dataList.get(i).get("_id").toString();
            details.append("'id':'"+ id+"',");
            String title = dataList.get(i).get("title").toString();
            details.append("'title':'"+ title+"',");
            String description =  dataList.get(i).get("description").toString();
            details.append("'description':'"+ description+"',");
            String by =  dataList.get(i).get("by").toString();
            details.append("'by':'"+ by+"',");
            String imageUrl = dataList.get(i).get("imageUrl").toString();
            details.append("'imageUrl':'"+ imageUrl+"'");
            details.append("}");

            if(i==dataList.size()-1){
                details.append("]");
                break;
            }else{
                details.append(",{");
            }
            i++;

        }//for loop end
        String retVal =  details.toString();
        retVal = retVal.replaceAll("'", String.valueOf('"'));
        LOG.info("Exiting getDetails()");
        return retVal;
    } //main function end

    @RequestMapping(value ="/details/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public String getSpecificDetails(@PathVariable String id){

        LOG.info("Entering into getSpecificDetails()");

        if(id.isEmpty()){
            String message="{'message':'One or more field(s) are empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }
        // Retrieving a collection
        MongoCollection<Document> collection = getCollection("mongoDb","productCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        List<Document> dataList = (List<Document>) collection.find(Filters.eq("_id",id.trim())).into(
                new ArrayList<Document>());

        if(dataList.size()==0){
            String message="{'message':'No result found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }
        StringBuffer details = new StringBuffer();
        details.append("[{");
        int i = 0;
        for (Document items : dataList) {
            String _id = dataList.get(i).get("_id").toString();
            details.append("'id':'"+ _id+"',");
            String title = dataList.get(i).get("title").toString();
            details.append("'title':'"+ title+"',");
            String description =  dataList.get(i).get("description").toString();
            details.append("'description':'"+ description+"',");
            String by =  dataList.get(i).get("by").toString();
            details.append("'by':'"+ by+"',");
            String imageUrl = dataList.get(i).get("imageUrl").toString();
            details.append("'imageUrl':'"+ imageUrl+"'");
            details.append("}");
            if(i==dataList.size()-1){
                details.append("]");
                break;
            }else{
                details.append(",{");
            }
            i++;
        }//for loop end
        String retVal =  details.toString();
        retVal = retVal.replaceAll("'", String.valueOf('"'));
        LOG.info("Exiting getSpecificDetails()");
        return retVal;
    }

    @RequestMapping(value ="/details/{id}", method = RequestMethod.PUT)
    @CrossOrigin
    public String updateSpecificDetails(@PathVariable String id,
                                        @RequestBody ItemDto itemDto){

        LOG.info("Entering updateSpecificDetails()");

        if(itemDto.getId().isEmpty()||itemDto.getTitle().isEmpty()||itemDto.getDescription().isEmpty()||itemDto.getBy().isEmpty()||itemDto.getImageUrl().isEmpty()){
            String message="{'message':'One or more field(s) are empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        MongoCollection<Document> collection = getCollection("mongoDb","productCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }
        Bson query = new Document("_id",id);

        Document document = new Document("title",itemDto.getTitle().trim())
                .append("description", itemDto.getDescription().trim())
                .append("by", itemDto.getBy().trim())
                .append("imageUrl", itemDto.getImageUrl().trim())
                .append("_id",itemDto.getId().trim());

        Document result = collection.findOneAndReplace(query,document);

        if(result==null||result.size()==0){
            String message="{'message':'Input data/field not Found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }
        LOG.info("Exiting updateSpecificDetails()");
        return getSpecificDetails(id);
    }

    @RequestMapping(value ="/details/{id}", method = RequestMethod.DELETE)
    @CrossOrigin
    public String deleteSpecificDetails(@PathVariable String id){
        LOG.info("Entering deleteSpecificDetails()");

        if(id.isEmpty()){
            String message="{'message':'Id is empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        MongoCollection<Document> collection = getCollection("mongoDb","productCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        Bson query = new Document("_id",id.trim());
        String deletedData = getSpecificDetails(id);
        Document result = collection.findOneAndDelete(query);

        if(result==null || result.size()==0){
            String message="{'message':'Input data/field not Found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        LOG.info("Exiting deleteSpecificDetails()");
        return deletedData;

    }

    @RequestMapping(value ="/details", method = RequestMethod.POST)
    @CrossOrigin
    @ResponseBody

    public String addSpecificDetails    (@RequestBody ItemDto itemDto ){

        LOG.info("Entering addSpecificDetails()");

        if(itemDto.getId().isEmpty()||itemDto.getTitle().isEmpty()||itemDto.getDescription().isEmpty()||itemDto.getBy().isEmpty()||itemDto.getImageUrl().isEmpty()){
            String message="{'message':'One or more field(s) are empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        MongoCollection<Document> collection = getCollection("mongoDb","productCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        Document document = new Document("title",itemDto.getTitle().trim())
                .append("description", itemDto.getDescription().trim())
                .append("by", itemDto.getBy().trim())
                .append("imageUrl", itemDto.getImageUrl().trim())
                .append("_id",itemDto.getId().trim());

        collection.insertOne(document);
        LOG.info("Exiting addSpecificDetails()");
        return getSpecificDetails(itemDto.getId().trim());

    }
    private MongoCollection<Document> getCollection(String dbName,String collectionName){

        MongoCollection<Document> collection = null;
        MongoClient mongo = null;
        try{

        // Creating a Mongo client
        mongo = new MongoClient( "localhost" , 27017 );

        // Creating Credentials
        MongoCredential credential;
        credential = MongoCredential.createCredential("sampleUser", dbName,
                "password".toCharArray());
        System.out.println("Connected to the database successfully");

        // Accessing the database
        MongoDatabase database = mongo.getDatabase(dbName);
        // Retrieving a collection
        collection = database.getCollection(collectionName);
        System.out.println("Collection " + collectionName + " selected successfully");
    }catch(MongoException e){
            mongo.close();
        LOG.info("Exception is connecting to database");
    }
        return  collection;
    }

    @RequestMapping(value ="/menuDetails", method = RequestMethod.GET)
    @CrossOrigin
    public String getMenuDetails(){
        LOG.info("Entering getMenuDetails()");
        MongoCollection<Document> collection = getCollection("groceryDb","menuCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        FindIterable<Document> matchingPlants = collection.find();
        LOG.info("Exiting getMenuDetails()");
        String menuDetails = JSON.serialize(matchingPlants);
        menuDetails = menuDetails.substring(53);
        menuDetails = "[{".concat(menuDetails);
        //menuDetails = menuDetails.replace("]}]","]}");
        return menuDetails;
    }
/*
    @RequestMapping(value ="/storeDetails", method = RequestMethod.GET)
    @CrossOrigin
    public String getStoreDetails(){

        LOG.info("Entering into getStoreDetails()");
        // Retrieving a collection
        MongoCollection<Document> collection = getCollection("mongoDb","zipCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

       List<Document> dataList = (List<Document>) collection.find().into(
                new ArrayList<Document>());


        if(dataList.size()==0){
            String message="{'message':'No result found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        StringBuffer details = new StringBuffer();
        details.append("[{");

        int i = 0;
        List<ZipBean> zips = new ArrayList<ZipBean>();

        for (Document items : dataList) {

            items.remove("_id");
           List<String> keySet = new ArrayList<String>(dataList.get(i).keySet());

           for(String key:keySet){
               details.append("'zipcode':'"+ items.get(key)+"',");

           }
            details.append("}");
            System.out.println(details.toString());
            if(i==dataList.size()-1){
                details.append("]");
                break;
            }else{
                details.append(",{");
            }
            i++;

        }//for loop end
        String retVal =  details.toString();
        retVal = retVal.replaceAll("'", String.valueOf('"'));
        LOG.info("Exiting getStoreDetails()");
        return retVal;
    } //main function end*/

  @RequestMapping(value ="/storeDetails/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public String getStoreSpecificDetails(@PathVariable String id){

        LOG.info("Entering into getStoreSpecificDetails()");

        if(id.isEmpty()){
            String message="{'message':'One or more field(s) are empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }
        // Retrieving a collection
        MongoCollection<Document> collection = getCollection("mongoDb","zipCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        List<Document> dataList = (List<Document>) collection.find(Filters.eq("zipCode",id.trim())).into(
                new ArrayList<Document>());

        if(dataList.size()==0){
            String message="{'message':'No result found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }
       StringBuffer details = new StringBuffer();
        details.append("[{");
        int i = 0;
      String zipcode = dataList.get(i).get("zipCode").toString();
      details.append("'zipCode':'"+ zipcode+"','stores':[{");
        for (Document items : dataList) {
            String city = dataList.get(i).get("city").toString();
            details.append("'city':'"+ city+"',");
            String area =  dataList.get(i).get("area").toString();
            details.append("'area':'"+ area+"'");
            details.append("}");
            if(i==dataList.size()-1){
                details.append("]}]");
                break;
            }else{
                details.append(",{");
            }
            i++;
        }//for loop end

        String retVal =  details.toString();
        retVal = retVal.replaceAll("'", String.valueOf('"'));
        LOG.info("Exiting getStoreSpecificDetails()");
        return retVal;
    }

   /* @RequestMapping(value ="/storeDetails/{id}", method = RequestMethod.PUT)
    @CrossOrigin
    public String updateStoreDetails(@PathVariable String id,
                                     @RequestBody ZipBean zipBean){

        LOG.info("Entering updateStoreDetails()");

        if(zipBean.getZipCode().isEmpty()||zipBean.getCity().isEmpty()||zipBean.getArea().isEmpty()){
            String message="{'message':'One or more field(s) are empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        MongoCollection<Document> collection = getCollection("mongoDb","zipCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        Bson query = new Document("zipCode",id.trim());

        Document document = new Document("zipCode",zipBean.getZipCode().trim())
                .append("area", zipBean.getArea().trim())
                .append("city", zipBean.getCity().trim());

        Document result = collection.findOneAndReplace(query,document);

        if(result==null||result.size()==0){
            String message="{'message':'Input data/field not Found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }
        LOG.info("Exiting updateStoreDetails()");
        return getStoreSpecificDetails(id);
    }*/

 /* @RequestMapping(value ="/storeDetails/{id}", method = RequestMethod.DELETE)
    @CrossOrigin
    public String deleteStoreDetails(@PathVariable String zipCode){
        LOG.info("Entering deleteStoreDetails()");

        if(zipCode.isEmpty()){
            String message="{'message':'Zip Code is empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        MongoCollection<Document> collection = getCollection("mongoDb","zipCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        Bson query = new Document("zipCode",zipCode.trim());
        String deletedData = getStoreSpecificDetails(zipCode);

        Document result = collection.findOneAndDelete(query);

        if(result==null || result.size()==0){
            String message="{'message':'Input data/field not Found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        LOG.info("Exiting deleteStoreDetails()");
        return deletedData;

    }
*/
    @RequestMapping(value ="/storeDetails", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public String addStoreDetails    (@RequestBody ZipBean zipBean ){

        LOG.info("Entering addStoreDetails()");

        if(zipBean.getZipCode().isEmpty()||zipBean.getCity().isEmpty()||zipBean.getArea().isEmpty()){
            String message="{'message':'One or more field(s) are empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

        MongoCollection<Document> collection = getCollection("mongoDb","zipCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            return message;
        }

          Document document = new Document("city",zipBean.getCity().trim())
                .append("area", zipBean.getArea().trim())
                .append("zipCode",zipBean.getZipCode().trim());

        collection.insertOne(document);

        LOG.info("Exiting addStoreDetails()");
       return getStoreSpecificDetails(zipBean.getZipCode().trim());
      }
}//class end