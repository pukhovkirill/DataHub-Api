package com.pukhovkirill.datahub.infrastructure.file.repository;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FtpGatewayImplTest {

    private FtpGatewayImpl ftpGateway;
    private FTPClient mockClient;
    private StorageEntityFactory mockFactory;
    private StorageEntity mockEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockClient = Mockito.mock(FTPClient.class);
        mockFactory = Mockito.mock(StorageEntityFactory.class);
        mockEntity = Mockito.mock(StorageEntity.class);
        ftpGateway = new FtpGatewayImpl(mockClient, mockFactory);
    }

    @Test
    public void testCount() throws IOException {
        FTPFile[] mockFiles = new FTPFile[3];
        when(mockClient.listFiles()).thenReturn(mockFiles);

        long count = ftpGateway.count();
        assertEquals(3, count);
    }

    @Test
    public void testDelete() throws IOException {
        when(mockClient.deleteFile(anyString())).thenReturn(true);

        ftpGateway.delete(mockEntity);

        verify(mockClient).deleteFile(mockEntity.getPath());
    }

    @Test
    public void testFindByPath() throws Exception {
        String mockPath = "test.txt";
        File mockFile = new File(mockPath);
        OutputStream mockOutputStream = Mockito.mock(OutputStream.class);
        FTPFile mockFtpFile = Mockito.mock(FTPFile.class);

        // Simulate retrieving a file
        when(mockClient.mlistFile(mockPath)).thenReturn(mockFtpFile);
        when(mockFtpFile.getTimestamp()).thenReturn(Calendar.getInstance());
        when(mockFtpFile.getSize()).thenReturn(1024L);

        // Mock StorageEntityFactory behavior
        StorageEntity mockEntity = Mockito.mock(StorageEntity.class);
        when(mockFactory.restore(anyString(), any(Timestamp.class), anyLong(), any(byte[].class)))
                .thenReturn(mockEntity);

        Optional<StorageEntity> result = ftpGateway.findByPath(mockPath);
        assertTrue(result.isPresent());

        verify(mockClient).retrieveFile(eq(mockPath), any(OutputStream.class));
    }

    @Test
    public void testSave() throws Exception {
        File mockLocalFile = Mockito.mock(File.class);
        when(mockLocalFile.getPath()).thenReturn("local.txt");
        when(mockEntity.getPath()).thenReturn("local.txt");
        when(mockEntity.getName()).thenReturn("remote.txt");
        when(mockEntity.getData()).thenReturn(new byte[] {1, 2, 3});

        when(mockClient.storeFile(eq("remote.txt"), any(FileInputStream.class))).thenReturn(true);

        StorageEntity result = ftpGateway.save(mockEntity);
        assertEquals(mockEntity, result);

        verify(mockClient).storeFile(eq("remote.txt"), any(FileInputStream.class));
    }

    @Test
    public void testExistsByPath() throws Exception {
        FTPFile mockFile = new FTPFile();
        mockFile.setName("test.txt");
        when(mockClient.listFiles()).thenReturn(new FTPFile[] {mockFile});

        boolean exists = ftpGateway.existsByPath("test.txt");
        assertTrue(exists);
    }
}
