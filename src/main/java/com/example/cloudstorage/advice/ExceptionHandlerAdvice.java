package com.example.cloudstorage.advice;

import com.example.cloudstorage.exceptions.InputDataErrorException;
import com.example.cloudstorage.exceptions.InternalServiceErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(InputDataErrorException.class)
    public ResponseEntity<String> inputDataErrorExceptionHandler(Exception e) {
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(e.getMessage());
    }

    @ExceptionHandler(InternalServiceErrorException.class)
    public ResponseEntity<String> internalServiceErrorExceptionHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(e.getMessage());
    }
}
