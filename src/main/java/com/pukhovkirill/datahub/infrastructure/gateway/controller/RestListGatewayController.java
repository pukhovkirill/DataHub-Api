package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import java.util.Collection;

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
    public ResponseEntity<Collection<String>> list(){
        var collection = ongoingGatewayService.list();
        return ResponseEntity.ok(collection);
    }

}
