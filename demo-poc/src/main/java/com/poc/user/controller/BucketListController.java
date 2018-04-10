package com.poc.user.controller;

import com.poc.user.model.CreateUserResponse;
import com.poc.user.model.SequenceId;
import com.poc.user.model.Task;
import com.poc.user.model.UpdateUserTask;
import com.poc.user.repository.TaskRepository;

import io.swagger.annotations.ApiOperation;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/bucketlist" , produces = "application/json")
public class BucketListController {


    @Autowired
    private MongoTemplate mongoTemplate;
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final TaskRepository taskRepository;
    @Autowired
    private MongoOperations mongoOperation;

    public BucketListController(TaskRepository userRepository) {
        this.taskRepository = userRepository;
    }

    //Returns all tasks present in the bucket list.
    @ApiOperation(value = "Task List")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Task> getAllTasksInBucket() {
        LOG.info("Getting all Tasks in the Bucket list.");
        return taskRepository.findAll();
    }


    //Adds tasks in the bucket list.
    @RequestMapping(value = "/createBucketList", method = RequestMethod.POST)
    public ResponseEntity<CreateUserResponse> addNewTaskstoBucketList(@RequestBody Task task) {

        CreateUserResponse cur = new CreateUserResponse();
        Query query = new Query(Criteria.where("_id").is("taskId"));
        Update update = new Update();
        update.inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        SequenceId seqId =
                mongoOperation.findAndModify(query, update, options, SequenceId.class);
        task.setId(Long.toString(seqId.getSeq()));

        LOG.info("Saving Task in Bucket List.");
        taskRepository.save(task);
        cur.setMessage("Task Created Successfully");
        return ResponseEntity.ok(cur);

    }


    //Fetches tasks of a given owner present in the bucket list.
    @RequestMapping(value = "/{owner}", method = RequestMethod.GET)
    public List<Task> getTaskByOwner(@PathVariable String owner) {
        LOG.info("Getting Task with Owner: {}.", owner);
        Query query = new Query();
        query.addCriteria(Criteria.where("owner").is(owner));
        List<Task> userTasks =  mongoTemplate.find(query, Task.class);
        if(null!=userTasks && userTasks.size() > 0){
            return userTasks;
        }else {
            LOG.info("No tasks are present in this owner's bucket list");
            return null;
        }
    }


    //Fetches all tasks except one with a given owner present in the bucket list.
    @RequestMapping(value = "/notin/{owner}", method = RequestMethod.GET)
    public List<Task> getTask(@PathVariable String owner) {
        LOG.info("Getting user with Owner: {}.", owner);
        List<Task> bucketList = taskRepository.findAll();
        List<Task> bucketListExcepOne = new ArrayList<Task>();
        for(Task currentTask  : bucketList)
        {
            if(currentTask.getOwner().equalsIgnoreCase(owner)){
                continue;
            }
            else {
                bucketListExcepOne.add(currentTask);
            }
        }
        return bucketListExcepOne;
    }



    //Deletes the tasks with a given id present in the bucket list.
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResponseEntity<CreateUserResponse> deleteTask(@PathVariable String id) {
        LOG.info("Deleting Task with ID: {}.", id);
        Object obj = null;

        CreateUserResponse cur = new CreateUserResponse();
        obj = taskRepository.findOne(id);
        if(obj != null) {
            LOG.info("Deleting user.");
            taskRepository.delete(id);
            cur.setMessage("Task Deleted Successfully");
            return ResponseEntity.ok(cur);
        }
        cur.setMessage("Task Id Doesnt Not Exist");
        return ResponseEntity.ok(cur);
    }

    //Updates the tasks with a given id and owner to newOwner
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<CreateUserResponse> updateTaskOwner(@RequestBody UpdateUserTask updateUserTask) {

        CreateUserResponse cur = new CreateUserResponse();
        //Validations
        if((null==updateUserTask.getId()) || (updateUserTask.getId().length() ==0))
        {
            cur.setMessage("Id cannot be null");
            return ResponseEntity.ok(cur);
        }
        if((null==updateUserTask.getOldOwner()) || (updateUserTask.getOldOwner().length() == 0))
        {
            cur.setMessage("Old Owner cannot be null");
            return ResponseEntity.ok(cur);
        }
        if((null==updateUserTask.getNewOwner()) || (updateUserTask.getNewOwner().length() ==0))
        {
            cur.setMessage("New Owner cannot be null");
            return ResponseEntity.ok(cur);
        }


        LOG.info("Updating Task with ID: {}.", updateUserTask.getId());
        Object obj = null;

        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").exists(true).andOperator(Criteria.where("id").is(updateUserTask.getId()),Criteria.where("owner").is(updateUserTask.getOldOwner())));

        obj = mongoTemplate.findOne(findQuery, Task.class);
        if(obj != null) {

            LOG.info("Updating owner of task to new Owner.");

            Update update = new Update();
            update.set("owner", updateUserTask.getNewOwner());

            mongoTemplate.updateMulti(findQuery, update, Task.class);

            cur.setMessage("Task Owner Updated Successfully");
            return ResponseEntity.ok(cur);
        }
        cur.setMessage("Task with given Id or Owner Doesnt Not Exist");
        return ResponseEntity.ok(cur);

    }

}