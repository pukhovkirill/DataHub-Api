package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;

@RestController
public class RestListGatewayController {

    private final OngoingGatewayService ongoingGatewayService;

    public RestListGatewayController(OngoingGatewayService ongoingGatewayService) {
        this.ongoingGatewayService = ongoingGatewayService;
    }

    @RequestMapping(value = "api/gateways/list", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> list(){
        var collection = ongoingGatewayService.list();
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.OK.value(),
                "data", collection));
    }

}
