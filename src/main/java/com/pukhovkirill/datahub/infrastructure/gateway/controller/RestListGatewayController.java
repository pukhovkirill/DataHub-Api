package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pukhovkirill.datahub.util.CredentialsSaver;

@RestController
public class RestListGatewayController {

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            List of all gateways registered by the user.\s
                            """,
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schemaProperties = {
                                            @SchemaProperty(name = "timestamp", schema = @Schema(implementation = Timestamp.class)),
                                            @SchemaProperty(name = "status", schema = @Schema(implementation = int.class)),
                                            @SchemaProperty(name = "gateways", array = @ArraySchema(schema = @Schema(implementation = String.class))
                                            ),

                                    })
                    }
            ),
    })
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
                "gateways", gateways));
    }

}
