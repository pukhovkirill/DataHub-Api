package com.pukhovkirill.datahub.infrastructure.external;

import lombok.Getter;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

@Getter
public class FtpManager {

    private final String server;

    private final int port;

    private final String user;

    private final String password;

    private final String workingDirectory;

    private FTPClient client;

    public FtpManager(String server, int port, String user, String password, String workingDirectory) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
        this.workingDirectory = workingDirectory;
    }

    public FtpManager(String server, int port, String user, String password) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
        this.workingDirectory = "storage";
    }

    public void connect() {
        if(client == null) {
            client = new FTPClient();
            try{
                client.connect(server, port);
                client.login(user, password);

                if(!client.changeWorkingDirectory(workingDirectory)){
                    client.makeDirectory(workingDirectory);
                    client.changeWorkingDirectory(workingDirectory);
                }
                client.setFileType(FTP.BINARY_FILE_TYPE);
            }catch(IOException e) {
                throw new RuntimeException("Failed to connect to FTP server", e);
            }
        }
    }

    public void disconnect() {
        try{
            if(client != null && client.isConnected()) {
                client.logout();
                client.disconnect();
            }
        }catch(IOException e) {
            throw new RuntimeException("Failed to disconnect from FTP server", e);
        }
    }
}
