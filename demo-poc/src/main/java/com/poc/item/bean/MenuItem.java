package com.poc.item.bean;
import com.poc.item.bean.*;
import org.springframework.data.annotation.Id;

public class MenuItem {

    @Id
    private String id;
    private Departments dept;
    public Departments getDept(){
        return dept;
    }

    public void setDept(Departments dept){
        this.dept = dept;
    }
    public String getId(){
        return id;
    }

    public void setURL(String id){
        this.id = id;
    }
}
