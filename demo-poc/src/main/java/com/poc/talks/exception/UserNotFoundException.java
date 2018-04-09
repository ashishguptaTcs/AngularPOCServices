package com.poc.talks.exception;



public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }


}
