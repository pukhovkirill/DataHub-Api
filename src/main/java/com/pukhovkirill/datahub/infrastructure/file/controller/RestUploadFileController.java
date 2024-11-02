package com.pukhovkirill.datahub.infrastructure.file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestUploadFileController {

    @RequestMapping(value = "api/files", method = RequestMethod.POST)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file){
        throw new RuntimeException("not implemented");
    }
}
