package com.pukhovkirill.datahub.infrastructure.external;

import com.jcraft.jsch.*;
import lombok.Getter;

import java.io.IOException;

@Getter
public class SftpClient{

    private final String server;

    private final int port;

    private final String user;

    private final String password;

    private final String workingDirectory;

    private ChannelSftp client;

    public SftpClient(String server, int port, String user, String password, String workingDirectory) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
        this.workingDirectory = workingDirectory;
    }

    public SftpClient(String server, int port, String user, String password) {
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

                client = (ChannelSftp) jschSession.openChannel("sftp");

                if(!client.pwd().equals(workingDirectory)){
                    client.mkdir(workingDirectory);
                    client.cd(workingDirectory);
                }
            }catch(JSchException | SftpException e) {
                throw new RuntimeException("Failed to connect to SFTP server", e);
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
