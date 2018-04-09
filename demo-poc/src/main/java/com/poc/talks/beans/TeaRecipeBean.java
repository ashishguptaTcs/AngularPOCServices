package com.poc.talks.beans;

import java.util.List;


public class TeaRecipeBean {

    private String id;
    private String name;
    private Boolean water;
    private Boolean milk;
    private Boolean sugar;
    private String brand;
    private int cupSize;
    private List<String> ingredients;
    private String createdBy;

    public TeaRecipeBean() {
    }

    public TeaRecipeBean(String id, String name, Boolean water, Boolean milk, Boolean sugar, String brand, int cupSize, List<String> ingredients, String createdBy) {
        this.id = id;
        this.name = name;
        this.water = water;
        this.milk = milk;
        this.sugar = sugar;
        this.brand = brand;
        this.cupSize = cupSize;
        this.ingredients = ingredients;
        this.createdBy = createdBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getWater() {
        return water;
    }

    public void setWater(Boolean water) {
        this.water = water;
    }

    public Boolean getMilk() {
        return milk;
    }

    public void setMilk(Boolean milk) {
        this.milk = milk;
    }

    public Boolean getSugar() {
        return sugar;
    }

    public void setSugar(Boolean sugar) {
        this.sugar = sugar;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getCupSize() {
        return cupSize;
    }

    public void setCupSize(int cupSize) {
        this.cupSize = cupSize;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
