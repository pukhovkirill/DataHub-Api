package com.pukhovkirill.datahub.infrastructure.file.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pukhovkirill.datahub.util.StringHelper;
import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.infrastructure.file.service.StorageService;
import com.pukhovkirill.datahub.infrastructure.file.exception.InvalidParamException;

@RestController
public class RestUploadFileController extends RestFileController{

    private final StorageService storageService;
    
    public RestUploadFileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(value = "api/files", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) throws IOException {
        if(file == null)
            throw new InvalidParamException("file is null", HttpStatus.INTERNAL_SERVER_ERROR);
        else if(file.isEmpty())
            throw new InvalidParamException("file is empty", HttpStatus.BAD_REQUEST);

        if(path == null)
            throw new InvalidParamException("path is null", HttpStatus.INTERNAL_SERVER_ERROR);
        else if(path.isEmpty() || path.isBlank())
            throw new InvalidParamException("path is empty", HttpStatus.BAD_REQUEST);
        else if (!pathIsValid(path))
            throw new InvalidParamException("path is invalid", HttpStatus.BAD_REQUEST);

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

        return ResponseEntity.ok().body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.OK.value(),
                "message", "success"));
    }
}
