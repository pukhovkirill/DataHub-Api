package com.pukhovkirill.datahub.infrastructure.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class GatewayCredentials {

    private String key;

    private String protocol;

    private String server;

    private int port;

    private String username;

    private String password;

    private String workingDirectory;

}
