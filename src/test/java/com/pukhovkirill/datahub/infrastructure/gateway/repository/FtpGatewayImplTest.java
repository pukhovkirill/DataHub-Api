package com.pukhovkirill.datahub.infrastructure.gateway.repository;

import java.io.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;

import com.pukhovkirill.datahub.infrastructure.external.FtpManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.infrastructure.exception.FTPFileAlreadyExistsException;
import com.pukhovkirill.datahub.infrastructure.exception.FTPFileNotFoundException;

class FtpGatewayImplTest {

    @Mock
    private FtpManager manager;

    @Mock
    private FTPClient client;

    @Mock
    private StorageEntityFactory factory;

    @InjectMocks
    private FtpGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        manager = Mockito.mock(FtpManager.class);
        client = Mockito.mock(FTPClient.class);

        when(manager.getClient()).thenReturn(client);

        factory = Mockito.mock(StorageEntityFactory.class);
        gateway = new FtpGatewayImpl(manager, factory);
    }

    @Test
    public void testCount() throws IOException {
        FTPFile[] mockFiles = new FTPFile[3];
        when(client.listFiles()).thenReturn(mockFiles);

        long count = gateway.count();
        assertEquals(3, count);
    }

    @Test
    public void testDelete() throws IOException {
        StorageEntity storageEntity = Mockito.mock(StorageEntity.class);
        when(client.deleteFile(anyString())).thenReturn(true);

        String path = "local.txt";

        FTPFile ftpFile = Mockito.mock(FTPFile.class);

        when(client.listFiles()).thenReturn(new FTPFile[] { ftpFile });
        when(ftpFile.getName()).thenReturn(path);

        when(storageEntity.getPath()).thenReturn(path);

        gateway.delete(storageEntity);

        verify(client).deleteFile(storageEntity.getPath());
    }

    @Test
    public void testDeleteWithFTPFileNotFoundException() throws IOException {
        StorageEntity storageEntity = Mockito.mock(StorageEntity.class);
        String path = "invalid.txt";

        when(storageEntity.getPath()).thenReturn(path);

        FTPFile[] ftpFiles = new FTPFile[0];
        when(client.listFiles()).thenReturn(ftpFiles);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.delete(storageEntity)
        );

        Assertions.assertEquals(String.format("Could not find file with name '%s'", path), exception.getCause().getMessage());
        Assertions.assertInstanceOf(FTPFileNotFoundException.class, exception.getCause());
    }

    @Test
    public void testDeleteWithIOException() throws IOException {
        StorageEntity storageEntity = mock(StorageEntity.class);
        String path = "local.txt";

        when(storageEntity.getPath()).thenReturn(path);

        FTPFile ftpFile = Mockito.mock(FTPFile.class);

        when(client.listFiles()).thenReturn(new FTPFile[] { ftpFile });
        when(ftpFile.getName()).thenReturn("local.txt");

        when(client.deleteFile(anyString())).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.delete(storageEntity)
        );

        Assertions.assertEquals("Error deleting file", exception.getCause().getMessage());
        Assertions.assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    public void testFindByPath() throws Exception {
        FTPFile ftpFile = Mockito.mock(FTPFile.class);
        String path = "test.txt";

        when(ftpFile.getTimestamp()).thenReturn(Calendar.getInstance());
        when(ftpFile.getSize()).thenReturn(1024L);
        when(ftpFile.getName()).thenReturn(path);

        when(client.mlistFile(path)).thenReturn(ftpFile);
        when(client.listFiles()).thenReturn(new FTPFile[] { ftpFile });

        StorageEntity mockEntity = Mockito.mock(StorageEntity.class);
        when(factory.restore(any(), any(Timestamp.class), anyLong(), any(byte[].class)))
                .thenReturn(mockEntity);

        Optional<StorageEntity> result = gateway.findByPath(path);
        assertTrue(result.isPresent());

        verify(client).retrieveFile(eq(path), any(OutputStream.class));
    }

    @Test
    public void testFindByPathWithFTPFileNotFoundException() throws Exception {
        String path = "invalid.txt";
        FTPFile[] ftpFiles = new FTPFile[0];
        when(client.listFiles()).thenReturn(ftpFiles);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.findByPath(path)
        );

        Assertions.assertEquals(String.format("Could not find file with name '%s'", path), exception.getCause().getMessage());
        Assertions.assertInstanceOf(FTPFileNotFoundException.class, exception.getCause());
    }

    @Test
    public void testSave() throws Exception {
        StorageEntity storageEntity = Mockito.mock(StorageEntity.class);
        String path = "local.txt";

        FTPFile[] ftpFiles = new FTPFile[0];
        when(client.listFiles()).thenReturn(ftpFiles);

        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getName()).thenReturn(path);
        when(storageEntity.getData()).thenReturn(new byte[] {1, 2, 3});

        when(client.storeFile(eq(path), any(ByteArrayInputStream.class))).thenReturn(true);

        StorageEntity result = gateway.save(storageEntity);
        assertEquals(storageEntity, result);

        verify(client).storeFile(eq("local.txt"), any(ByteArrayInputStream.class));
    }

    @Test
    public void testSaveWithFTPFileAlreadyExistsException() throws IOException {
        StorageEntity storageEntity = Mockito.mock(StorageEntity.class);
        String path = "local.txt";

        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getData()).thenReturn(new byte[] {1, 2, 3});

        FTPFile ftpFile = Mockito.mock(FTPFile.class);

        when(client.listFiles()).thenReturn(new FTPFile[] { ftpFile });
        when(ftpFile.getName()).thenReturn(path);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.save(storageEntity)
        );

        Assertions.assertEquals(String.format("File with the name '%s' already exists", path), exception.getCause().getMessage());
        Assertions.assertInstanceOf(FTPFileAlreadyExistsException.class, exception.getCause());
    }

    @Test
    public void testSaveWithIOException() throws IOException {
        StorageEntity storageEntity = mock(StorageEntity.class);
        String path = "local.txt";

        FTPFile[] ftpFiles = new FTPFile[0];
        when(client.listFiles()).thenReturn(ftpFiles);

        when(storageEntity.getName()).thenReturn(path);
        when(storageEntity.getData()).thenReturn(new byte[] {1, 2, 3});
        when(client.storeFile(anyString(), any())).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.save(storageEntity)
        );

        Assertions.assertEquals("Error loading file", exception.getCause().getMessage());
        Assertions.assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    public void testExistsByPath() throws Exception {
        FTPFile ftpFile = new FTPFile();
        ftpFile.setName("test.txt");
        when(client.listFiles()).thenReturn(new FTPFile[] {ftpFile});

        boolean exists = gateway.existsByPath("test.txt");
        assertTrue(exists);
    }

    @Test
    public void testExistsByPathWhenNotFound() throws Exception {
        when(client.listFiles()).thenReturn(new FTPFile[] {});

        boolean exists = gateway.existsByPath("test.txt");
        assertFalse(exists);
    }
}
