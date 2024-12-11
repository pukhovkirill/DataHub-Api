package com.pukhovkirill.datahub.infrastructure.gateway.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class GatewayCredentials {

    @NotNull(message = "key is null")
    @NotEmpty(message = "key is empty")
    @NotBlank(message = "key is empty")
    private String key;

    @NotNull(message = "protocol is null")
    @NotEmpty(message = "protocol is empty")
    @NotBlank(message = "protocol is empty")
    private String protocol;

    @NotNull(message = "server is null")
    @NotEmpty(message = "server is empty")
    @NotBlank(message = "server is empty")
    private String server;

    @NotNull(message = "port is null")
    @Min(value = 0, message = "port out of range")
    @Max(value = 65535, message = "port out of range")
    private int port;

    @NotNull
    @NotEmpty(message = "username is empty")
    @NotBlank(message = "username is empty")
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String workingDirectory;

}
