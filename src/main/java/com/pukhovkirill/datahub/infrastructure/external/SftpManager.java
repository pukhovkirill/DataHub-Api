package com.pukhovkirill.datahub.infrastructure.external;

import java.io.IOException;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

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
                var jschSession = jsch.getSession(user, server, port);
                jschSession.setPassword(password);
                jschSession.connect();

                if(!jschSession.isConnected()){
                    LOGGER.error("Failed to login to SFTP server as {}", user);
                    throw new FailedServerLoginException("SFTP login failed", "sftp");
                }

                LOGGER.info("Connected to SFTP server: {}:{}", server, port);
                LOGGER.info("Logged in to SFTP server as {}", user);

                client = (ChannelSftp) jschSession.openChannel("sftp");

                try{
                    client.cd(workingDirectory);
                }catch(SftpException e){
                    LOGGER.info("Failed to change directory in {}", workingDirectory);
                    try{
                        client.mkdir(workingDirectory);
                    }catch(SftpException _e){
                        throw new IOException("Unable to create remote directory '" + workingDirectory + "'");
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
                client.disconnect();
        }catch(Exception e) {
            throw new RuntimeException("Failed to disconnect from SFTP server", e);
        }
    }
}
