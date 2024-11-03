package com.pukhovkirill.datahub.infrastructure.file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestDownloadFileController extends RestFileController {

    @RequestMapping(value = "api/files", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(@RequestParam("path") String path){
        throw new RuntimeException("not implemented");
    }
}
