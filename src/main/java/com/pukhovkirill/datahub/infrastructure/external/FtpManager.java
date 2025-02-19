package com.pukhovkirill.datahub.infrastructure.external;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;

import lombok.Getter;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pukhovkirill.datahub.infrastructure.gateway.exception.FailedServerLoginException;
import com.pukhovkirill.datahub.infrastructure.gateway.exception.FailedToServerConnectException;

@Getter
public class FtpManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(FtpManager.class);

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
                client.connect(InetAddress.getByName(server), port);

                client.setKeepAlive(true);
                client.setControlKeepAliveTimeout(Duration.ofSeconds(5));
                client.setControlKeepAliveReplyTimeout(Duration.ofSeconds(10));

                client.setFileType(FTP.BINARY_FILE_TYPE);

                LOGGER.info("Connected to FTP server: {}:{}", server, port);

                if(!client.login(user, password)){
                    LOGGER.error("Failed to login to FTP server as {}", user);
                    throw new FailedServerLoginException("FTP login failed", "ftp");
                }

                LOGGER.info("Logged in to FTP server as {}", user);

                if(!client.changeWorkingDirectory(workingDirectory)){
                    LOGGER.info("Failed to change directory in {}", workingDirectory);

                    if (!client.makeDirectory(workingDirectory)) {
                        throw new IOException(
                                "Unable to create remote directory '" + workingDirectory + "'.  error='" + client.getReplyString()+"'"
                        );
                    }
                    LOGGER.info("Created remote directory {}", workingDirectory);
                    client.changeWorkingDirectory(workingDirectory);
                }

            }catch(IOException e) {
                throw new FailedToServerConnectException("Failed to connect to FTP server", "ftp", e);
            }
        }
    }

    public void disconnect() {
        try{
            if(client != null && client.isConnected()) {
                client.logout();
                client.disconnect();
                LOGGER.info("Disconnected from FTP server");
            }
        }catch(IOException e) {
            throw new RuntimeException("Failed to disconnect from FTP server", e);
        }
    }
}
