package com.pukhovkirill.datahub.infrastructure.file.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.pukhovkirill.datahub.entity.exception.StorageEntityAlreadyExistsException;
import com.pukhovkirill.datahub.entity.exception.StorageEntityNotFoundException;
import com.pukhovkirill.datahub.entity.exception.StorageGatewayAlreadyExistsException;
import com.pukhovkirill.datahub.entity.exception.StorageGatewayNotFoundException;

@ControllerAdvice
public class FileGlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleException(RuntimeException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleStorageEntityNotFoundException(StorageEntityNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleStorageEntityAlreadyExistsException(StorageEntityAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleStorageGatewayNotFoundException(StorageGatewayNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleStorageGatewayAlreadyExistsException(StorageGatewayAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
