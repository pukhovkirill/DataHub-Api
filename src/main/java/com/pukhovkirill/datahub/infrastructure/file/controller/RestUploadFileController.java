package com.pukhovkirill.datahub.infrastructure.file.controller;

import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.infrastructure.file.service.StorageService;
import com.pukhovkirill.datahub.util.StringHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.Timestamp;

@RestController
public class RestUploadFileController extends RestFileController{

    private final StorageService storageService;
    
    public RestUploadFileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(value = "api/files", method = RequestMethod.POST)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) throws IOException {
        if(file == null)
            return ResponseEntity.internalServerError().body("file is null");
        else if(file.isEmpty())
            return ResponseEntity.badRequest().body("file cannot be empty");

        if(path == null)
            return ResponseEntity.internalServerError().body("path is null");
        else if(path.isEmpty() || path.isBlank())
            return ResponseEntity.badRequest().body("path cannot be empty");
        else if (!pathIsValid(path))
            return ResponseEntity.badRequest().body("path is invalid");

        String location = path.split(":")[0];
        String filepath = path.split(":")[1];
        String filename = StringHelper.extractName(filepath);

        StorageFile storageFile = StorageFile.builder()
                .name(filename)
                .path(filepath)
                .contentType(URLConnection.guessContentTypeFromName(filename))
                .size(file.getSize())
                .lastModified(new Timestamp(System.currentTimeMillis()))
                .location(location)
                .build();

        this.storageService.uploadTo(location, storageFile, new ByteArrayInputStream(file.getBytes()));

        return ResponseEntity.ok().body("success");
    }
}
