package com.pukhovkirill.datahub.infrastructure.file.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

@RestController
public class RestSearchFileController extends RestFileController {

    @RequestMapping(value = "api/files/list", method = RequestMethod.GET)
    public ResponseEntity<List<StorageEntityDto>> searchAll(){
        throw new RuntimeException("not implemented");
    }
}
