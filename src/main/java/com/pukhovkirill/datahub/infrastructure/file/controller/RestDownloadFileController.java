package com.pukhovkirill.datahub.infrastructure.file.controller;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pukhovkirill.datahub.infrastructure.file.service.StorageService;
import com.pukhovkirill.datahub.infrastructure.file.exception.InvalidParamException;

@RestController
public class RestDownloadFileController extends RestFileController {

    private final StorageService storageService;

    public RestDownloadFileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(value = "api/files", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> download(@RequestParam("path") String path) throws IOException {
        if(path == null)
            throw new InvalidParamException("path is null", HttpStatus.INTERNAL_SERVER_ERROR);
        else if(path.isEmpty() || path.isBlank())
            throw new InvalidParamException("path is empty", HttpStatus.BAD_REQUEST);
        else if (!pathIsValid(path))
            throw new InvalidParamException("path is invalid", HttpStatus.BAD_REQUEST);

        String location = path.split(":")[0];
        String filepath = path.split(":")[1];

        ByteArrayOutputStream baos = storageService.download(location, filepath);
        byte[] bytes = baos.toByteArray();
        baos.close();

        return ResponseEntity.ok().body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.OK.value(),
                "data", bytes));
    }
}
