package com.poc.talks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(TeaNotFoundException.class)
    public ResponseEntity<ExceptionResponse> teaNotFound(TeaNotFoundException e){
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Not Found");
        response.setErrorMessage(e.getMessage());
        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity <ExceptionResponse> userNotFound(UserNotFoundException e){
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Not Found");
        response.setErrorMessage(e.getMessage());
        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity <ExceptionResponse> invalidCredentials(InvalidCredentialsException e){
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unauthorized");
        response.setErrorMessage(e.getMessage());
        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity <ExceptionResponse> userAlreadyExistsException(UserAlreadyExistsException e){
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("User Already Exists");
        response.setErrorMessage(e.getMessage());
        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.CONFLICT);
    }
}
