package com.pukhovkirill.datahub.infrastructure.file.handler;

import com.pukhovkirill.datahub.entity.exception.StorageEntityAlreadyExistsException;
import com.pukhovkirill.datahub.entity.exception.StorageEntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class StorageEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleStorageEntityNotFoundException(StorageEntityNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleStorageEntityAlreadyExistsException(StorageEntityAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

}
