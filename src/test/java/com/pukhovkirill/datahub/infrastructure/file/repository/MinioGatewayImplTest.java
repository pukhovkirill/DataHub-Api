package com.pukhovkirill.datahub.infrastructure.file.repository;

import com.pukhovkirill.datahub.entity.exception.StorageEntityAlreadyExistsException;
import io.minio.*;
import io.minio.errors.*;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;

import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.MinIOContainer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;
import com.pukhovkirill.datahub.entity.model.StorageEntity;

public class MinioGatewayImplTest {

    private static MinIOContainer minIOContainer;

    private static MinioClient client;

    @InjectMocks
    private MinioGatewayImpl gateway;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        gateway = new MinioGatewayImpl(client, new StorageEntityFactoryImpl());
        ReflectionTestUtils.setField(gateway, "BUCKET_NAME", "storage");
    }

    @BeforeAll
    public static void init(){
        minIOContainer = new MinIOContainer("minio/minio")
                .withUserName("bf24e339e96f0c056c1b685807c0ba6496d5a6f637f2")
                .withPassword("7341c0b12ef3faa77bfd9525918a325a18e1a40b9c6f7ff3a2497c23fc067a1f")
                .withEnv("MINIO_DEFAULT_BUCKETS", "storage")
                .withExposedPorts(9000);

        minIOContainer.start();

        client = MinioClient
                .builder()
                .endpoint(minIOContainer.getS3URL())
                .credentials(minIOContainer.getUserName(), minIOContainer.getPassword())
                .build();

        try {
            client.makeBucket(MakeBucketArgs.builder().bucket("storage").build());
        }catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e){
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void after(){
        minIOContainer.stop();
    }

    @Test
    public void testSave() throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        String path = "/some/path";
        byte[] data = "test data".getBytes();

        StorageEntity storageEntity = mock(StorageEntity.class);

        when(storageEntity.getData()).thenReturn(data);
        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getSize()).thenReturn((long) data.length);

        gateway.save(storageEntity);

        var baos = new ByteArrayOutputStream();

        InputStream is = client.getObject(
                GetObjectArgs.builder()
                        .bucket("storage")
                        .object(path)
                        .build());

        byte[] buff = new byte[1024];

        int count;
        while ((count = is.read(buff)) >= 0){
            baos.write(buff, 0, count);
        }

        Assertions.assertArrayEquals(data, baos.toByteArray());
    }

    public byte[] testSaveWithExistsExceptionSetUp(String path) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = "test data".getBytes();

        var putBuilder = PutObjectArgs.builder()
                .bucket("storage")
                .object(path)
                .stream(new ByteArrayInputStream(data), data.length, -1);

        client.putObject(putBuilder.build());

        return data;
    }

    @Test
    public void testSaveWithStorageEntityAlreadyExistsException() throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        String path = "/some/path";

        byte[] data = testSaveWithExistsExceptionSetUp(path);

        StorageEntity storageEntity = mock(StorageEntity.class);

        when(storageEntity.getData()).thenReturn(data);
        when(storageEntity.getPath()).thenReturn(path);
        when(storageEntity.getSize()).thenReturn((long) data.length);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.save(storageEntity)
        );

        Assertions.assertEquals(String.format("Storage entity with the name '%s' already exists", path), exception.getMessage());
        Assertions.assertInstanceOf(StorageEntityAlreadyExistsException.class, exception);
    }

    @Test
    public void testSaveWithMinioException() {
        StorageEntity storageEntity = mock(StorageEntity.class);

        when(storageEntity.getData()).thenThrow(new RuntimeException(new MinioException("invalid secret key")));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.save(storageEntity)
        );

        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(MinioException.class, exception.getCause());
    }

    @Test
    public void testSaveWithIOException(){
        StorageEntity storageEntity = mock(StorageEntity.class);

        when(storageEntity.getData()).thenThrow(new RuntimeException(new IOException("invalid data")));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.save(storageEntity)
        );

        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(IOException.class, exception.getCause());
    }

    public byte[] testFindByPathSetUp(String path) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = "test data".getBytes();

        var putBuilder = PutObjectArgs.builder()
                .bucket("storage")
                .object(path)
                .stream(new ByteArrayInputStream(data), data.length, -1);

        client.putObject(putBuilder.build());

        return data;
    }

    @Test
    public void testFindByPath() throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        String path = "/test/path";
        byte[] data = testFindByPathSetUp(path);

        var result = gateway.findByPath(path);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(path, result.get().getPath());
        Assertions.assertArrayEquals(data, result.get().getData());
    }

    @Test
    public void testFindByPathFileWithMinioException(){
        String path = "/invalid/path";

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gateway.findByPath(path)
        );

        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(MinioException.class, exception.getCause());
    }


    @Test
    public void testExistsByPath() throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        String path = "/test/path";
        testFindByPathSetUp(path);

        var result = gateway.existsByPath(path);
        Assertions.assertTrue(result);
    }

    @Test
    public void testExistsByPathWithFalseResult(){
        String path = "/invalid/path";

        var result = gateway.existsByPath(path);
        Assertions.assertFalse(result);
    }

    @Test
    public void testDelete() throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        String path = "/test/path";
        testFindByPathSetUp(path);

        StorageEntity storageEntity = mock(StorageEntity.class);

        when(storageEntity.getPath()).thenReturn(path);

        gateway.delete(storageEntity);

        try{
            client.statObject(StatObjectArgs.builder()
                    .bucket("storage")
                    .object(path).build());
        }catch (MinioException e){
            Assertions.assertEquals("Object does not exist", e.getMessage());
        }
    }
}
