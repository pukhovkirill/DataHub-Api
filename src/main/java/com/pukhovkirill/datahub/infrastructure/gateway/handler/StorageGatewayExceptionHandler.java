package com.pukhovkirill.datahub.infrastructure.gateway.handler;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.pukhovkirill.datahub.infrastructure.gateway.exception.FailedServerLoginException;
import com.pukhovkirill.datahub.infrastructure.gateway.exception.FailedToServerConnectException;
import com.pukhovkirill.datahub.entity.exception.StorageGatewayAlreadyExistsException;
import com.pukhovkirill.datahub.entity.exception.StorageGatewayNotFoundException;

@ControllerAdvice
public class StorageGatewayExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleStorageGatewayNotFoundException(StorageGatewayNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Not Found",
                "message", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleStorageGatewayAlreadyExistsException(StorageGatewayAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.CONFLICT.value(),
                "error", "Conflict",
                "message", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleFailedToServerConnectException(FailedToServerConnectException e){
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.BAD_GATEWAY.value(),
                "error", "Bad Gateway",
                "message", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleFailedServerLoginException(FailedServerLoginException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", "UNAUTHORIZED",
                "message", e.getMessage()));
    }

}
