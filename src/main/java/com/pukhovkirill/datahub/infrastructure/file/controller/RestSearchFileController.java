package com.pukhovkirill.datahub.infrastructure.file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class RestSearchFileController {

    @RequestMapping(value = "api/files/list", method = RequestMethod.GET)
    public ResponseEntity<String> searchAll(){
        throw new RuntimeException("not implemented");
    }

}
