package com.pukhovkirill.datahub.infrastructure.file.repository;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FtpGatewayImplTest {

    @Mock
    private FTPClient mockClient;

    @Mock
    private StorageEntityFactory mockFactory;

    @InjectMocks
    private FtpGatewayImpl ftpGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockClient = Mockito.mock(FTPClient.class);
        mockFactory = Mockito.mock(StorageEntityFactory.class);
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
        StorageEntity mockEntity = Mockito.mock(StorageEntity.class);
        when(mockClient.deleteFile(anyString())).thenReturn(true);

        ftpGateway.delete(mockEntity);

        verify(mockClient).deleteFile(mockEntity.getPath());
    }

    @Test
    public void testFindByPath() throws Exception {
        String mockPath = "test.txt";
        FTPFile mockFtpFile = Mockito.mock(FTPFile.class);

        when(mockClient.mlistFile(mockPath)).thenReturn(mockFtpFile);
        when(mockFtpFile.getTimestamp()).thenReturn(Calendar.getInstance());
        when(mockFtpFile.getSize()).thenReturn(1024L);

        StorageEntity mockEntity = Mockito.mock(StorageEntity.class);
        when(mockFactory.restore(anyString(), any(Timestamp.class), anyLong(), any(byte[].class)))
                .thenReturn(mockEntity);

        Optional<StorageEntity> result = ftpGateway.findByPath(mockPath);
        assertTrue(result.isPresent());

        verify(mockClient).retrieveFile(eq(mockPath), any(OutputStream.class));
    }

    @Test
    public void testSave() throws Exception {
        StorageEntity mockEntity = Mockito.mock(StorageEntity.class);
        when(mockEntity.getPath()).thenReturn("local.txt");
        when(mockEntity.getName()).thenReturn("local.txt");
        when(mockEntity.getData()).thenReturn(new byte[] {1, 2, 3});

        when(mockClient.storeFile(eq("local.txt"), any(ByteArrayInputStream.class))).thenReturn(true);

        StorageEntity result = ftpGateway.save(mockEntity);
        assertEquals(mockEntity, result);

        verify(mockClient).storeFile(eq("local.txt"), any(ByteArrayInputStream.class));
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
