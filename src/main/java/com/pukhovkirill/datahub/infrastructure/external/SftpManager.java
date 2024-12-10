package com.pukhovkirill.datahub.infrastructure.external;

import java.io.IOException;

import com.jcraft.jsch.*;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pukhovkirill.datahub.infrastructure.gateway.exception.FailedServerLoginException;
import com.pukhovkirill.datahub.infrastructure.gateway.exception.FailedToServerConnectException;

@Getter
public class SftpManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(SftpManager.class);

    private final String server;

    private final int port;

    private final String user;

    private final String password;

    private final String workingDirectory;

    private ChannelSftp client;

    @Getter(AccessLevel.NONE)
    private Session jschSession;

    public SftpManager(String server, int port, String user, String password, String workingDirectory) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
        this.workingDirectory = workingDirectory;
    }

    public SftpManager(String server, int port, String user, String password) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
        this.workingDirectory = "storage";
    }

    public void connect() {
        if(client == null) {
            try{
                var jsch = new JSch();
                jsch.setKnownHosts("~/.ssh/known_hosts");
                jschSession = jsch.getSession(user, server, port);
                jschSession.setConfig("StrictHostKeyChecking", "no");
                jschSession.setConfig("PreferredAuthentications", "password");
                jschSession.setServerAliveInterval(5000);
                jschSession.setServerAliveCountMax(5);
                jschSession.setPassword(password);
                jschSession.connect(10000);

                if(!jschSession.isConnected()){
                    LOGGER.error("Failed to login to SFTP server as {}", user);
                    throw new FailedServerLoginException("SFTP login failed", "sftp");
                }

                client = (ChannelSftp) jschSession.openChannel("sftp");
                client.connect(5000);

                LOGGER.info("Connected to SFTP server: {}:{}", server, port);
                LOGGER.info("Logged in to SFTP server as {}", user);

                try{
                    client.cd(workingDirectory);
                }catch(SftpException e){
                    LOGGER.info("Failed to change directory in {}", workingDirectory);
                    try{
                        client.mkdir(workingDirectory);
                    }catch(SftpException _e){
                        LOGGER.error("Unable to create remote directory '{}'", workingDirectory);
                        throw new IOException();
                    }
                    LOGGER.info("Created remote directory {}", workingDirectory);
                    client.cd(workingDirectory);
                }
            }catch(JSchException | SftpException | IOException e) {
                throw new FailedToServerConnectException("Failed to connect to SFTP server", "sftp", e);
            }
        }
    }

    public void disconnect() {
        try{
            if(client != null && client.isConnected())
                client.exit();
            if(jschSession != null && jschSession.isConnected())
                jschSession.disconnect();
        }catch(Exception e) {
            throw new RuntimeException("Failed to disconnect from SFTP server", e);
        }
    }
}
