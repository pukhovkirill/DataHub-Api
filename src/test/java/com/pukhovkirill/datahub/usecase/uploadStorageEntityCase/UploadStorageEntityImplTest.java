package com.pukhovkirill.datahub.usecase.uploadStorageEntityCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactoryImpl;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.infrastructure.TestConfig;
import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

@SpringBootTest(classes = TestConfig.class)
public class UploadStorageEntityImplTest {
    @Mock
    private StorageGateway storageGateway;

    @Mock
    private StorageEntityFactoryImpl factory;

    @InjectMocks
    private UploadStorageEntityImpl uploadStorageEntity;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        uploadStorageEntity = new UploadStorageEntityImpl(storageGateway);
        ReflectionTestUtils.setField(uploadStorageEntity, "factory", factory);
    }

    @Test
    public void testUpload(){
        StorageFile dto = StorageFile.builder()
                .name("testFile.txt")
                .path("/test/path")
                .contentType("text/plain")
                .location("/location")
                .lastModified(new Timestamp(System.currentTimeMillis()))
                .build();

        byte[] data = "test data".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);

        StorageEntity storageEntity = mock(StorageEntity.class);
        when(factory.create(anyString(), any())).thenReturn(storageEntity);

        uploadStorageEntity.upload(dto, bais);

        verify(storageGateway).save(eq(storageEntity));
    }

    @Test
    public void testUploadWithIOException() throws IOException {
        StorageEntityDto dto = mock(StorageFile.class);
        ByteArrayInputStream bais = mock(ByteArrayInputStream.class);

        when(bais.read(any())).thenThrow(new RuntimeException(new IOException("Simulated IOException")));

        RuntimeException exception = Assert.assertThrows(
                RuntimeException.class,
                () -> uploadStorageEntity.upload(dto, bais)
        );

        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof IOException);
    }
}
