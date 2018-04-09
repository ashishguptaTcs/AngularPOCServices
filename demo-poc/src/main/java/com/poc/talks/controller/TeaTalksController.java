package com.poc.talks.controller;

import com.poc.talks.beans.TeaRecipeBean;
import com.poc.talks.beans.UserDetails;
import com.poc.talks.exception.InvalidCredentialsException;
import com.poc.talks.exception.TeaNotFoundException;
import com.poc.talks.exception.UserAlreadyExistsException;
import com.poc.talks.exception.UserNotFoundException;
import com.poc.talks.service.LoginService;
import com.poc.talks.service.TeaTalksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/teaTalks")
public class TeaTalksController {

    @Autowired
    private TeaTalksService teaTalksService;

    @Autowired
    private LoginService login;

    @RequestMapping(method = RequestMethod.GET, value = "/getTeaList/{userId}")
    public List getTeaList(@PathVariable("userId") String userId){
        List teaList = new ArrayList();
        teaList = teaTalksService.getTeaList(userId);
        if(teaList.size() == 0) throw new TeaNotFoundException("No Tea Recipe found against user " + userId );
        return teaList;
    }

    @RequestMapping(value = "/addTea", method = RequestMethod.POST)
    public String addNewTea(@RequestBody TeaRecipeBean teaBean){

        return teaTalksService.addNewTea(teaBean);
    }

    @RequestMapping(value = "/getTea/{id}/{userId}" , method = RequestMethod.GET)
    public List getTea(@PathVariable("id") String id, @PathVariable("userId") String userId){
        return teaTalksService.getTea(id, userId);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public UserDetails login(@RequestBody UserDetails user){
        return login.logging(user);
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signup(@RequestBody UserDetails user){
        return login.signup(user);
    }

    /*@RequestMapping(value = "/addTeaToDB", method = RequestMethod.POST)
    public String add(@RequestBody TeaRecipeBean teaRecipe){
        return teaTalksService.addNewTeaToDB(teaRecipe);
    }

    @RequestMapping(value = "/getTeaListFromDB/{user}", method = RequestMethod.GET)
    public List getList(@PathVariable("user") String user){
        List teaList = teaTalksService.getTeaListFromDB(user);
        if(teaList.size() == 0) throw new TeaNotFoundException("No Tea Recipe found against user " + user );
        return teaList;
    }

    @RequestMapping(value = "/getTeaFromDB/{id}/{userId}", method = RequestMethod.GET)
    public List getTeaFromDB(@PathVariable("id") String id, @PathVariable("userId") String userId){
        List tea = teaTalksService.getTeaFromDB(id, userId);
        if(tea.size() == 0) throw new TeaNotFoundException("No Tea Recipe found against id " + id );
        return tea;
    }*/
}
