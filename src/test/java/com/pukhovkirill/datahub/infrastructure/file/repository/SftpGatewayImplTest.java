package com.pukhovkirill.datahub.infrastructure.file.repository;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.util.ArrayIterator;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SftpGatewayImplTest {

    @Mock
    private ChannelSftp client;

    @Mock
    private StorageEntityFactory factory;

    @InjectMocks
    private SftpGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        client = Mockito.mock(ChannelSftp.class);
        factory = Mockito.mock(StorageEntityFactory.class);
        gateway = new SftpGatewayImpl(client, factory);
    }

    @Test
    void testCount() throws Exception {
        Vector<ChannelSftp.LsEntry> files = mock(Vector.class);
        when(files.size()).thenReturn(1);

        when(client.pwd()).thenReturn("/home/sftp");
        when(client.ls("/home/sftp")).thenReturn(files);

        long count = gateway.count();

        assertEquals(1, count);
        verify(client).connect();
        verify(client).exit();
    }

    @Test
    void testCountWhenRuntimeException() throws Exception {
        when(client.pwd()).thenThrow(new SftpException(0, "Error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.count()
        );

        Assertions.assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void testDelete() throws Exception {
        StorageEntity storageEntity = mock(StorageEntity.class);

        String path = "/home/sftp/test.txt";

        when(storageEntity.getPath()).thenReturn(path);

        gateway.delete(storageEntity);

        verify(client).connect();
        verify(client).rm(path);
        verify(client).exit();
    }

    @Test
    void testDeleteWithRuntimeException() throws Exception {
        StorageEntity entity = mock(StorageEntity.class);

        String path = "/home/sftp/test.txt";

        when(entity.getPath()).thenReturn(path);

        doThrow(new SftpException(0, "Error")).when(client).rm(anyString());

        assertThrows(RuntimeException.class, () -> gateway.delete(entity));
    }

    @Test
    void testFindByPath() throws Exception {
        StorageEntity storageEntity = mock(StorageEntity.class);

        String path = "/home/sftp/test.txt";

        SftpATTRS attrs = mock(SftpATTRS.class);

        when(client.lstat(anyString())).thenReturn(attrs);
        when(factory.restore(anyString(), any(Timestamp.class), anyLong(), any())).thenReturn(storageEntity);

        Optional<StorageEntity> result = gateway.findByPath(path);

        assertTrue(result.isPresent());
        verify(client).connect();
        verify(client).exit();
    }

    @Test
    void testFindByPathWithRuntimeException() throws Exception {
        String path = "/home/sftp/test.txt";
        when(client.lstat(path)).thenThrow(new SftpException(0, "Error"));

        assertThrows(RuntimeException.class, () -> gateway.findByPath(path));
    }

    @Test
    void testSaveFile() throws Exception {
        StorageEntity entity = mock(StorageEntity.class);

        String path = "/home/sftp/test.txt";

        when(entity.getPath()).thenReturn(path);
        when(entity.getData()).thenReturn(new byte[]{1, 2, 3});

        StorageEntity result = gateway.save(entity);

        assertEquals(entity, result);
        verify(client).connect();
        verify(client).put(any(ByteArrayInputStream.class), eq(path));
        verify(client).exit();
    }

    @Test
    void testExistsByPath() throws Exception {
        ChannelSftp.LsEntry file = mock(ChannelSftp.LsEntry.class);

        String dir = "/home/sftp";
        String path = "/home/sftp/test.txt";

        when(file.getLongname()).thenReturn(path);

        Iterator<ChannelSftp.LsEntry> iter = new ArrayIterator<>(new ChannelSftp.LsEntry[]{file});

        Vector<ChannelSftp.LsEntry> files = mock(Vector.class);
        when(files.size()).thenReturn(1);
        when(files.iterator()).thenReturn(iter);

        when(client.pwd()).thenReturn(dir);
        when(client.ls(dir)).thenReturn(files);

        boolean exists = gateway.existsByPath(path);

        assertTrue(exists);
        verify(client).connect();
        verify(client).exit();
    }

    @Test
    void testExistsByPathWhenNotFound() throws Exception {
        String path = "/home/sftp/test.txt";

        Iterator<ChannelSftp.LsEntry> iter = new ArrayIterator<>(new ChannelSftp.LsEntry[]{});

        Vector<ChannelSftp.LsEntry> files = mock(Vector.class);
        when(files.size()).thenReturn(1);
        when(files.iterator()).thenReturn(iter);

        boolean exists = gateway.existsByPath(path);

        assertFalse(exists);
        verify(client).connect();
        verify(client).exit();
    }
}