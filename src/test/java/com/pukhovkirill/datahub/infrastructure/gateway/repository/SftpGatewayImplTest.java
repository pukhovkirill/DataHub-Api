package com.pukhovkirill.datahub.infrastructure.gateway.repository;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.*;

import com.pukhovkirill.datahub.infrastructure.external.SftpManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.util.ArrayIterator;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.infrastructure.exception.SFTPFileAlreadyExistsException;
import com.pukhovkirill.datahub.infrastructure.exception.SFTPFileNotFoundException;

class SftpGatewayImplTest {

    @Mock
    private SftpManager manager;

    @Mock
    private ChannelSftp client;

    @Mock
    private StorageEntityFactory factory;

    @InjectMocks
    private SftpGatewayImpl gateway;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        manager = Mockito.mock(SftpManager.class);
        client = Mockito.mock(ChannelSftp.class);

        when(manager.getClient()).thenReturn(client);

        factory = Mockito.mock(StorageEntityFactory.class);
        gateway = new SftpGatewayImpl(manager, factory);
    }

    @Test
    public void testCount() throws Exception {
        Vector<ChannelSftp.LsEntry> files = mock(Vector.class);
        when(files.size()).thenReturn(1);

        when(client.pwd()).thenReturn("/home/sftp");
        when(client.ls("/home/sftp")).thenReturn(files);

        long count = gateway.count();

        assertEquals(1, count);
    }

    @Test
    public void testCountWhenRuntimeException() throws Exception {
        when(client.pwd()).thenThrow(new SftpException(0, "Error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.count()
        );

        Assertions.assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    public void testDelete() throws Exception {
        StorageEntity storageEntity = mock(StorageEntity.class);
        ChannelSftp.LsEntry file = mock(ChannelSftp.LsEntry.class);

        String dir = "/home/sftp/";
        String path = "/home/sftp/test.txt";

        when(file.getLongname()).thenReturn(path);

        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getData()).thenReturn(new byte[]{1, 2, 3});


        // this is the section for the method existsByPath()
        Iterator<ChannelSftp.LsEntry> iter = new ArrayIterator<>(new ChannelSftp.LsEntry[]{file});
        Vector<ChannelSftp.LsEntry> files = mock(Vector.class);
        when(files.size()).thenReturn(1);
        when(files.iterator()).thenReturn(iter);

        when(client.pwd()).thenReturn(dir);
        when(client.ls(dir)).thenReturn(files);
        // end

        when(storageEntity.getPath()).thenReturn(path);

        gateway.delete(storageEntity);

        verify(client).rm(path);
    }

    @Test
    public void testDeleteWithRuntimeException() throws Exception {
        StorageEntity storageEntity = mock(StorageEntity.class);

        String path = "/home/sftp/test.txt";

        when(storageEntity.getPath()).thenReturn(path);

        doThrow(new SftpException(0, "Error")).when(client).rm(anyString());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.delete(storageEntity)
        );

        Assertions.assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    public void testDeleteWithSFTPFileNotFoundException() throws SftpException {
        StorageEntity storageEntity = mock(StorageEntity.class);
        String dir = "/home/sftp/";
        String path = "/home/sftp/invalid.txt";

        when(storageEntity.getPath()).thenReturn(path);

        // this is the section for the method existsByPath()
        Iterator<ChannelSftp.LsEntry> iter = new ArrayIterator<>(new ChannelSftp.LsEntry[]{});
        Vector<ChannelSftp.LsEntry> files = mock(Vector.class);
        when(files.size()).thenReturn(0);
        when(files.iterator()).thenReturn(iter);

        when(client.pwd()).thenReturn(dir);
        when(client.ls(dir)).thenReturn(files);
        // end

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.delete(storageEntity)
        );

        Assertions.assertEquals(String.format("Could not find file with name '%s'", path), exception.getMessage());
        Assertions.assertInstanceOf(SFTPFileNotFoundException.class, exception);
    }

    @Test
    public void testFindByPath() throws Exception {
        StorageEntity storageEntity = mock(StorageEntity.class);
        ChannelSftp.LsEntry file = mock(ChannelSftp.LsEntry.class);

        String dir = "/home/sftp/";
        String path = "/home/sftp/test.txt";

        when(file.getLongname()).thenReturn(path);

        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getData()).thenReturn(new byte[]{1, 2, 3});


        // this is the section for the method existsByPath()
        Iterator<ChannelSftp.LsEntry> iter = new ArrayIterator<>(new ChannelSftp.LsEntry[]{file});
        Vector<ChannelSftp.LsEntry> files = mock(Vector.class);
        when(files.size()).thenReturn(1);
        when(files.iterator()).thenReturn(iter);

        when(client.pwd()).thenReturn(dir);
        when(client.ls(dir)).thenReturn(files);
        // end

        SftpATTRS attrs = mock(SftpATTRS.class);

        when(client.lstat(anyString())).thenReturn(attrs);
        when(factory.restore(anyString(), any(Timestamp.class), anyLong(), any())).thenReturn(storageEntity);

        Optional<StorageEntity> result = gateway.findByPath(path);

        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByPathWithRuntimeException() throws Exception {
        String path = "/home/sftp/test.txt";
        when(client.lstat(path)).thenThrow(new SftpException(0, "Error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.findByPath(path)
        );

        Assertions.assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    public void testFindByPathWithSFTPFileNotFoundException() throws SftpException {
        String dir = "/home/sftp/";
        String path = "/home/sftp/invalid.txt";

        // this is the section for the method existsByPath()
        Iterator<ChannelSftp.LsEntry> iter = new ArrayIterator<>(new ChannelSftp.LsEntry[]{});
        Vector<ChannelSftp.LsEntry> files = mock(Vector.class);
        when(files.size()).thenReturn(0);
        when(files.iterator()).thenReturn(iter);

        when(client.pwd()).thenReturn(dir);
        when(client.ls(dir)).thenReturn(files);
        // end

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.findByPath(path)
        );

        Assertions.assertEquals(String.format("Could not find file with name '%s'", path), exception.getMessage());
        Assertions.assertInstanceOf(SFTPFileNotFoundException.class, exception);
    }

    @Test
    public void testSaveFile() throws Exception {
        StorageEntity storageEntity = mock(StorageEntity.class);

        String path = "/home/sftp/test.txt";

        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getData()).thenReturn(new byte[]{1, 2, 3});

        StorageEntity result = gateway.save(storageEntity);

        assertEquals(storageEntity, result);
        verify(client).put(any(ByteArrayInputStream.class), eq(path));
    }

    @Test
    public void testSaveFileWithSFTPFileAlreadyExistsException() throws SftpException {
        StorageEntity storageEntity = mock(StorageEntity.class);
        ChannelSftp.LsEntry file = mock(ChannelSftp.LsEntry.class);

        String dir = "/home/sftp/";
        String path = "/home/sftp/test.txt";

        when(file.getLongname()).thenReturn(path);

        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getData()).thenReturn(new byte[]{1, 2, 3});

        // this is the section for the method existsByPath()
        Iterator<ChannelSftp.LsEntry> iter = new ArrayIterator<>(new ChannelSftp.LsEntry[]{file});
        Vector<ChannelSftp.LsEntry> files = mock(Vector.class);
        when(files.size()).thenReturn(1);
        when(files.iterator()).thenReturn(iter);

        when(client.pwd()).thenReturn(dir);
        when(client.ls(dir)).thenReturn(files);
        // end

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.save(storageEntity)
        );

        Assertions.assertEquals(String.format("File with the name '%s' already exists", path), exception.getMessage());
        Assertions.assertInstanceOf(SFTPFileAlreadyExistsException.class, exception);
    }

    @Test
    public void testExistsByPath() throws Exception {
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
    }

    @Test
    public void testExistsByPathWhenNotFound() {
        String path = "/home/sftp/test.txt";

        Iterator<ChannelSftp.LsEntry> iter = new ArrayIterator<>(new ChannelSftp.LsEntry[]{});

        Vector<ChannelSftp.LsEntry> files = mock(Vector.class);
        when(files.size()).thenReturn(0);
        when(files.iterator()).thenReturn(iter);

        boolean exists = gateway.existsByPath(path);

        assertFalse(exists);
    }
}