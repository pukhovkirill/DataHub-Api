package com.pukhovkirill.datahub.infrastructure.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(max = 65535)
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
