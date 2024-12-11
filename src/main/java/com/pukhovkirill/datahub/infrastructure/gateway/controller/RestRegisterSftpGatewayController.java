package com.pukhovkirill.datahub.infrastructure.gateway.controller;

import java.sql.Timestamp;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.pukhovkirill.datahub.infrastructure.gateway.factory.StorageGatewayFactory;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;

@RestController
public class RestRegisterSftpGatewayController extends RestRegisterGatewayController{

    public RestRegisterSftpGatewayController(OngoingGatewayService ongoingGatewayService,
                                             @Qualifier("sftpGatewayFactory") StorageGatewayFactory factory) {
        super(ongoingGatewayService, factory);
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Register the SFTP gateway.\s
                            """,
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schemaProperties = {
                                            @SchemaProperty(name = "timestamp", schema = @Schema(implementation = Timestamp.class)),
                                            @SchemaProperty(name = "status", schema = @Schema(implementation = int.class)),

                                    })
                    }
            ),
    })
    @RequestMapping(value = "api/gateways/sftp", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> registerSftp(@RequestBody @Valid GatewayCredentials credentials) {
        return register(credentials);
    }
}
