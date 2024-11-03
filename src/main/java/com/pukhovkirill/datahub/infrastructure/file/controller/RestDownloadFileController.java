package com.pukhovkirill.datahub.infrastructure.file.controller;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pukhovkirill.datahub.infrastructure.file.service.StorageService;

@RestController
public class RestDownloadFileController extends RestFileController {

    private final StorageService storageService;

    public RestDownloadFileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(value = "api/files", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(@RequestParam("path") String path) throws IOException {
        if(path == null)
            throw new IllegalArgumentException("path is null");
        else if(path.isEmpty() || path.isBlank())
            throw new IllegalArgumentException("path cannot be empty");
        else if (!pathIsValid(path))
            throw new IllegalArgumentException("path is invalid");

        String location = path.split(":")[0];
        String filepath = path.split(":")[1];

        ByteArrayOutputStream baos = storageService.download(location, filepath);
        byte[] bytes = baos.toByteArray();
        baos.close();

        return ResponseEntity.ok(bytes);
    }
}
