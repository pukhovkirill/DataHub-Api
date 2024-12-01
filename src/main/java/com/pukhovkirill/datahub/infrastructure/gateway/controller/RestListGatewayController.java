package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pukhovkirill.datahub.util.CredentialsSaver;

@RestController
public class RestListGatewayController {

    @RequestMapping(value = "api/gateways/list", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> list(){
        List<String> gateways = new ArrayList<>();
        var credentials = CredentialsSaver.getInstance().loadCredentials();
        for(var credential : credentials) {
            gateways.add(credential.getKey());
        }
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.OK.value(),
                "data", gateways));
    }

}
