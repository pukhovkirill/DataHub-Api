package com.pukhovkirill.datahub.infrastructure.file.handler;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.pukhovkirill.datahub.infrastructure.file.exception.InvalidParamException;

@ControllerAdvice
public class ParamExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> invalidParamException(InvalidParamException e){
        return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", e.getStatus().value(),
                "error", e.getStatus().name(),
                "message", e.getMessage()));
    }

}
