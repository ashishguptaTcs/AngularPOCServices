package com.poc.talks.exception;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message){
        super(message);
    }
}
