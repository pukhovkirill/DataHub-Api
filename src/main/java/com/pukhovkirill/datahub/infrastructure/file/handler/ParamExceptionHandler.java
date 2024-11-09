package com.pukhovkirill.datahub.infrastructure.file.handler;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.pukhovkirill.datahub.infrastructure.file.exception.FileParamException;
import com.pukhovkirill.datahub.infrastructure.file.exception.PathParamException;

@ControllerAdvice
public class ParamExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> fileParamException(FileParamException e){
        return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", e.getStatus().value(),
                "error", e.getStatus().name(),
                "message", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> pathParamException(PathParamException e){
        return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", e.getStatus().value(),
                "error", e.getStatus().name(),
                "message", e.getMessage()));
    }

}
