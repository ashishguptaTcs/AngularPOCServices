package com.poc.item.bean;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Departments {

    private String url;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder() ;
        str.append("Value:- " + getValue());
        str.append(" Url:- " + getUrl());
        return str.toString();
    }
    }
