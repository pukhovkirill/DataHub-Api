package com.pukhovkirill.datahub.infrastructure.gateway.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;

import io.minio.GetObjectResponse;
import io.minio.ObjectWriteResponse;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import okhttp3.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pukhovkirill.datahub.entity.exception.StorageEntityAlreadyExistsException;
import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;
import com.pukhovkirill.datahub.entity.model.StorageEntity;

import io.minio.MinioClient;
import org.springframework.test.util.ReflectionTestUtils;

public class MinioGatewayImplTest {

    @Mock
    private MinioClient mockClient;

    @InjectMocks
    private MinioGatewayImpl gateway;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gateway = new MinioGatewayImpl(mockClient, new StorageEntityFactoryImpl());
        ReflectionTestUtils.setField(gateway, "BUCKET_NAME", "internal");
    }

    @Test
    public void testSave() throws Exception {
        String path = "/some/path";
        byte[] data = "test data".getBytes();

        StorageEntity storageEntity = mock(StorageEntity.class);
        when(storageEntity.getData()).thenReturn(data);
        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getSize()).thenReturn((long) data.length);

        when(mockClient.putObject(any())).thenReturn(mock(ObjectWriteResponse.class));


        gateway.save(storageEntity);


        verify(mockClient, times(1)).putObject(any());
    }

    @Test
    public void testSaveWithStorageEntityAlreadyExistsException() throws Exception {
        String path = "/some/path";
        byte[] data = "test data".getBytes();

        StorageEntity storageEntity = mock(StorageEntity.class);
        when(storageEntity.getData()).thenReturn(data);
        when(storageEntity.getPath()).thenReturn(path);

        when(mockClient.statObject(any())).thenReturn(mock(StatObjectResponse.class));


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.save(storageEntity)
        );


        assertNotNull(exception);
        assertEquals(String.format("Storage entity with the name '%s' already exists", path), exception.getMessage());
        assertInstanceOf(StorageEntityAlreadyExistsException.class, exception);
    }


    @Test
    public void testSaveWithMinioException() {
        StorageEntity storageEntity = mock(StorageEntity.class);

        when(storageEntity.getData()).thenThrow(new RuntimeException(new MinioException("Invalid secret key")));


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.save(storageEntity)
        );


        assertNotNull(exception.getCause());
        assertInstanceOf(MinioException.class, exception.getCause());
    }

    @Test
    public void testFindByPath() throws Exception {
        String path = "/test/path";
        byte[] data = "test data".getBytes();

        StatObjectResponse stat = mock(StatObjectResponse.class);
        when(stat.lastModified()).thenReturn(ZonedDateTime.now());
        when(stat.size()).thenReturn((long) data.length);

        when(mockClient.statObject(any())).thenReturn(stat);

        GetObjectResponse response = new GetObjectResponse(
                mock(Headers.class),
                "bucket",
                "region",
                "/test/path",
                new ByteArrayInputStream(data)
        );

        when(mockClient.getObject(any())).thenReturn(response);


        var result = gateway.findByPath(path);


        assertTrue(result.isPresent());
        assertEquals(path, result.get().getPath());
        assertArrayEquals(data, result.get().getData());
    }

    @Test
    public void testFindByPathWithException() throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        String path = "/invalid/path";
        when(mockClient.statObject(any())).thenReturn(mock(StatObjectResponse.class));
        when(mockClient.getObject(any())).thenThrow(new RuntimeException(new MinioException("Object not found")));


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.findByPath(path)
        );


        assertNotNull(exception);
        assertNotNull(exception.getCause());
        assertInstanceOf(MinioException.class, exception.getCause());
    }

    @Test
    public void testExistsByPath() throws Exception {
        String path = "/test/path";

        when(mockClient.statObject(any())).thenReturn(mock(StatObjectResponse.class));


        var result = gateway.existsByPath(path);


        assertTrue(result);
        verify(mockClient, times(1)).statObject(any());
    }

    @Test
    public void testExistsByPathWithFalseResult() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String path = "/invalid/path";

        when(mockClient.statObject(any())).thenThrow(new RuntimeException(new MinioException("Object not found")));


        var result = gateway.existsByPath(path);


        assertFalse(result);
    }

    @Test
    public void testDelete() throws Exception {
        String path = "/test/path";

        StorageEntity storageEntity = mock(StorageEntity.class);

        when(storageEntity.getPath()).thenReturn(path);
        doNothing().when(mockClient).removeObject(any());


        gateway.delete(storageEntity);


        verify(mockClient, times(1)).removeObject(any());
    }
}
