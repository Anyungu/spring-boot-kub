package com.chef.ingredients.exceptions;

import java.time.LocalDateTime;

import com.chef.ingredients.responses.StatusResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<StatusResponse> handleExceptions(Exception ex, WebRequest request) {
        StatusResponse statusResponse = new StatusResponse(LocalDateTime.now().plusHours(3), ex.getMessage(), 500);
        return new ResponseEntity<>(statusResponse, new HttpHeaders(), HttpStatus.OK);
    }

    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<StatusResponse> recordNotFoundExceptionHandling(CustomException ex,
            WebRequest request) {
        StatusResponse statusResponse = new StatusResponse(LocalDateTime.now().plusHours(3), ex.getMessage(),
                ex.getCode());
        return new ResponseEntity<>(statusResponse, new HttpHeaders(), HttpStatus.OK);
    }

}