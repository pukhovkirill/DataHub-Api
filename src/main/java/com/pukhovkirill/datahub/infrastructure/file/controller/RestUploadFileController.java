package com.pukhovkirill.datahub.infrastructure.file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class RestUploadFileController {

    @RequestMapping(value = "api/files", method = RequestMethod.POST)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file){
        throw new RuntimeException("not implemented");
    }
}
