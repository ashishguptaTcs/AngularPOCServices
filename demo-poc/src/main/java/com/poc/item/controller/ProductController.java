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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import org.slf4j.Logger;



@RestController
public class ProductController{

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    @Autowired
    public Environment environment;
    @RequestMapping(value ="/details", method = RequestMethod.GET)
    @CrossOrigin
    public String getDetails(){

        LOG.info("Entering into getDetails()");
        // Retrieving a collection
        MongoCollection<Document> collection = getCollection("mongoDb","productCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        List<Document> dataList = (List<Document>) collection.find().into(
                new ArrayList<Document>());

        if(dataList.size()==0){
            String message="{'message':'No result found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.info(message);
            return message;
        }

        StringBuffer details = new StringBuffer();
        details.append("[{");

        int index = 0;
        for (Document items : dataList) {
            String id = dataList.get(index).get("_id").toString();
            details.append("'id':'"+ id+"',");
            String title = dataList.get(index).get("title").toString();
            details.append("'title':'"+ title+"',");
            String description =  dataList.get(index).get("description").toString();
            details.append("'description':'"+ description+"',");
            String by =  dataList.get(index).get("by").toString();
            details.append("'by':'"+ by+"',");
            String imageUrl = dataList.get(index).get("imageUrl").toString();
            details.append("'imageUrl':'"+ imageUrl+"'");
            details.append("}");

            if(index==dataList.size()-1){
                details.append("]");
                break;
            }else{
                details.append(",{");
            }
            index++;

        }//for loop end
        String retVal =  details.toString();
        retVal = retVal.replaceAll("'", String.valueOf('"'));
        LOG.info("List of all products==> "+retVal);
        LOG.info("Exiting getDetails()");
        return retVal;
    } //main function end

    @RequestMapping(value ="/details/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public String getSpecificProduct(@PathVariable String id){

        LOG.info("Entering into getSpecificProduct()");

        if(id.isEmpty()){
            String message="{'message':'One or more field(s) are empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }
        // Retrieving a collection
        MongoCollection<Document> collection = getCollection("mongoDb","productCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        List<Document> dataList = (List<Document>) collection.find(Filters.eq("_id",id.trim())).into(
                new ArrayList<Document>());

        if(dataList.size()==0){
            String message="{'message':'No result found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.info(message);
            return message;
        }
        StringBuffer details = new StringBuffer();
        details.append("[{");
        int index = 0;
        for (Document items : dataList) {
            String _id = dataList.get(index).get("_id").toString();
            details.append("'id':'"+ _id+"',");
            String title = dataList.get(index).get("title").toString();
            details.append("'title':'"+ title+"',");
            String description =  dataList.get(index).get("description").toString();
            details.append("'description':'"+ description+"',");
            String by =  dataList.get(index).get("by").toString();
            details.append("'by':'"+ by+"',");
            String imageUrl = dataList.get(index).get("imageUrl").toString();
            details.append("'imageUrl':'"+ imageUrl+"'");
            details.append("}");
            if(index==dataList.size()-1){
                details.append("]");
                break;
            }else{
                details.append(",{");
            }
            index++;
        }//for loop end
        String retVal =  details.toString();
        retVal = retVal.replaceAll("'", String.valueOf('"'));
        LOG.info("List of queried product==> " + retVal);
        LOG.info("Exiting getSpecificProduct()");
        return retVal;
    }

    @RequestMapping(value ="/details/{id}", method = RequestMethod.PUT)
    @CrossOrigin
    public String updateProductDetails(@PathVariable String id,
                                        @RequestBody ItemDto itemDto){

        LOG.info("Entering updateProductDetails()");

        if(itemDto.getId().isEmpty()||itemDto.getTitle().isEmpty()||itemDto.getDescription().isEmpty()||itemDto.getBy().isEmpty()||itemDto.getImageUrl().isEmpty()){
            String message="{'message':'One or more field(s) are empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        if(!id.equals(itemDto.getId().trim())){
            String message="{'message':'Product id information is not matching'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        if(id.trim().length() > 10 || itemDto.getId().trim().length() > 10){
            String message="{'message':'Product id length should not be more than 10 characters'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        if(!itemDto.getImageUrl().startsWith("https://")){
            String message="{'message':'Invalid Image URL structure'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        MongoCollection<Document> collection = getCollection("mongoDb","productCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
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
            String message="{'message':'Input data not Found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.info(message);
            return message;
        }
        LOG.info("Exiting updateSpecificDetails()");
        return getSpecificProduct(id);
    }

    @RequestMapping(value ="/details/{id}", method = RequestMethod.DELETE)
    @CrossOrigin
    public String deleteProduct(@PathVariable String id){
        LOG.info("Entering deleteProduct()");

        if(id.isEmpty()){
            String message="{'message':'Id is empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        if(id.length()>10){
            String message="{'message':'Product id length should not be more than 10 characters'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        MongoCollection<Document> collection = getCollection("mongoDb","productCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        Bson query = new Document("_id",id.trim());
        String deletedData = getSpecificProduct(id);
        Document result = collection.findOneAndDelete(query);

        if(result==null || result.size()==0){
            String message="{'message':'Input data not Found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.info(message);
            return message;
        }

        LOG.info("Exiting deleteProduct()");
        LOG.info("Deleted product==> "+deletedData);
        return deletedData;

    }

    @RequestMapping(value ="/details", method = RequestMethod.POST)
    @CrossOrigin
    @ResponseBody

    public String addNewProduct (@RequestBody ItemDto itemDto ){

        LOG.info("Entering addNewProduct()");

        if(itemDto.getId().isEmpty()||itemDto.getTitle().isEmpty()||itemDto.getDescription().isEmpty()||itemDto.getBy().isEmpty()||itemDto.getImageUrl().isEmpty()){
            String message="{'message':'One or more field(s) are empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        if(itemDto.getId().length()>10){
            String message="{'message':'Product id length should not be more than 10 characters'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        if(!itemDto.getImageUrl().startsWith("http://")||!itemDto.getImageUrl().startsWith("https://")){
            String message="{'message':'Invalid Image URL structure'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        MongoCollection<Document> collection = getCollection("mongoDb","productCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        List<Document> dataList = (List<Document>) collection.find(Filters.eq("_id",itemDto.getId().trim())).into(
                new ArrayList<Document>());

        if(dataList.size()!=0){
            String message="{'message':'Item with id: " + itemDto.getId() + " already exists'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.info(message);
            return message;
        }

        Document document = new Document("title",itemDto.getTitle().trim())
                .append("description", itemDto.getDescription().trim())
                .append("by", itemDto.getBy().trim())
                .append("imageUrl", itemDto.getImageUrl().trim())
                .append("_id",itemDto.getId().trim());

        collection.insertOne(document);
        LOG.info("Exiting addNewProduct()");
        return getSpecificProduct(itemDto.getId().trim());

    }
    private MongoCollection<Document> getCollection(String dbName,String collectionName){

        MongoCollection<Document> collection = null;
        MongoClient mongo = null;
        try{

            // Creating a Mongo client
            mongo = new MongoClient(environment.getProperty("spring.data.mongodb.host") , Integer.valueOf(environment.getProperty("spring.data.mongodb.port")));

            // Creating Credentials
            MongoCredential credential;
            credential = MongoCredential.createCredential("sampleUser", dbName,
                    "password".toCharArray());
            LOG.info("Connected to the database successfully");

            // Accessing the database
            MongoDatabase database = mongo.getDatabase(dbName);
            // Retrieving a collection
            collection = database.getCollection(collectionName);
            LOG.info("Collection " + collectionName + " selected successfully");
        }catch(MongoException e){
            mongo.close();
            LOG.error("Exception is connecting to database");
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
            LOG.error(message);
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
    @RequestMapping(value ="/storeDetails/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public String getStoreSpecificDetails(@PathVariable String id){

        LOG.info("Entering into getStoreSpecificDetails()");

        if(id.trim().isEmpty()){
            String message="{'message':'Input zipcode is empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        try
        {
            double d = Double.parseDouble(id.trim());
        }
        catch(NumberFormatException nfe)
        {
            String message="{'message':'Input zipcode is not numeric'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        if(id.trim().length()<6 || id.trim().length()>6){
            String message="{'message':'Input zipcode length must be six digits'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        MongoCollection<Document> collection = getCollection("mongoDb","zipCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        List<Document> dataList = (List<Document>) collection.find(Filters.eq("zipCode",id.trim())).into(
                new ArrayList<Document>());

        if(dataList.size()==0){
            String message="{'message':'No result found'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.info(message);
            return message;
        }
        StringBuffer details = new StringBuffer();
        details.append("{");
        int index = 0;

        details.append("'stores':[{");
        for (Document items : dataList) {
            String city = dataList.get(index).get("city").toString();
            details.append("'city':'"+ city+"',");
            String area =  dataList.get(index).get("area").toString();
            details.append("'area':'"+ area+"'");
            details.append("}");
            if(index==dataList.size()-1){
                details.append("]}");
                break;
            }else{
                details.append(",{");
            }
            index++;
        }//for loop end

        String retVal =  details.toString();
        retVal = retVal.replaceAll("'", String.valueOf('"'));
        LOG.info("Mapped stored of input zipcode "+ id + " is:" +retVal);
        LOG.info("Exiting getStoreSpecificDetails()");
        return retVal;
    }

    @RequestMapping(value ="/storeDetails", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public String addStoreDetails    (@RequestBody ZipBean zipBean ){

        LOG.info("Entering addStoreDetails()");

        if(zipBean.getZipCode().isEmpty()||zipBean.getCity().isEmpty()||zipBean.getArea().isEmpty()){
            String message="{'message':'One or more field(s) are empty'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
            return message;
        }

        MongoCollection<Document> collection = getCollection("mongoDb","zipCollection");

        if(collection==null){
            String message="{'message':'Can not connect to database'}";
            message = message.replaceAll("'",String.valueOf('"'));
            LOG.error(message);
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