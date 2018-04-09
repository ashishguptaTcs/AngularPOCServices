package com.poc.talks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No Such Tea!")
public class TeaNotFoundException extends RuntimeException {

    public TeaNotFoundException(String message){
        super(message);
    }
}
