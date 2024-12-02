package com.pukhovkirill.datahub.infrastructure.file.controller;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.infrastructure.file.service.SearchService;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;

@RestController
public class RestSearchFileController extends RestFileController {

    private final SearchService searchService;

    private final OngoingGatewayService ongoingGateways;

    public RestSearchFileController(SearchService searchService, OngoingGatewayService gatewayService) {
        this.searchService = searchService;
        this.ongoingGateways = gatewayService;
    }

    @RequestMapping(value = "api/files/list", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> searchAll(){
        List<StorageEntityDto> files = new LinkedList<>();

        for(String gateway : ongoingGateways.list()){
            var list = searchService.list(gateway);
            files.addAll(list);
        }

        return ResponseEntity.ok().body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.OK.value(),
                "files", files));
    }
}
