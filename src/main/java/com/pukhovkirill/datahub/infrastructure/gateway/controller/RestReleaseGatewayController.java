package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;

@RestController
public class RestReleaseGatewayController {

    private final OngoingGatewayService ongoingGatewayService;

    public RestReleaseGatewayController(OngoingGatewayService ongoingGatewayService) {
        this.ongoingGatewayService = ongoingGatewayService;
    }

    @RequestMapping(value = "api/gateways", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> release(@RequestParam("key") String key) throws IOException {
        ongoingGatewayService.release(key);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.OK.value()));
    }
}
