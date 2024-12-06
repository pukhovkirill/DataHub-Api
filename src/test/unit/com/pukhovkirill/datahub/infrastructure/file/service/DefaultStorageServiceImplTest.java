package com.pukhovkirill.datahub.infrastructure.file.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.pukhovkirill.datahub.entity.model.StorageEntity;
import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.infrastructure.gateway.repository.MinioGatewayImpl;
import com.pukhovkirill.datahub.infrastructure.gateway.service.OngoingGatewayService;
import com.pukhovkirill.datahub.usecase.deleteStorageEntityCase.DeleteStorageEntity;
import com.pukhovkirill.datahub.usecase.downloadStorageEntityCase.DownloadStorageEntity;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.ListStorageEntity;
import com.pukhovkirill.datahub.usecase.uploadStorageEntityCase.UploadStorageEntity;

class DefaultStorageServiceImplTest {

    @Mock
    private OngoingGatewayService ongoingGateways;

    @Mock
    private BeanFactory beanFactory;

    @InjectMocks
    private DefaultStorageServiceImpl defaultStorageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUploadTo() {
        StorageEntityDto entity = mock(StorageEntityDto.class);
        String name = "/file";

        ByteArrayInputStream bais = new ByteArrayInputStream("data".getBytes());

        ListStorageEntity listUseCase = mock(ListStorageEntity.class);
        when(listUseCase.list()).thenReturn(List.of());
        when(beanFactory.getBean(eq(ListStorageEntity.class), any())).thenReturn(listUseCase);

        UploadStorageEntity uploadUseCase = mock(UploadStorageEntity.class);
        when(beanFactory.getBean(eq(UploadStorageEntity.class), any())).thenReturn(uploadUseCase);

        when(ongoingGateways.get(anyString())).thenReturn(mock(MinioGatewayImpl.class));
        when(entity.getName()).thenReturn(name);

        defaultStorageService.uploadTo("internal", entity, bais);

        verify(uploadUseCase, times(1)).upload(entity, bais);
    }

    @Test
    public void testUploadToWhenEntityAlreadyExists() {
        String location = "internal";
        String path = "/file";

        StorageEntity mockEntity = mock(StorageEntity.class);
        when(mockEntity.getPath()).thenReturn(path);

        Collection<StorageEntity> entities = new ArrayList<>();
        entities.add(mockEntity);

        ListStorageEntity listUseCase = mock(ListStorageEntity.class);
        when(listUseCase.list()).thenReturn(entities);
        when(beanFactory.getBean(eq(ListStorageEntity.class), any())).thenReturn(listUseCase);

        UploadStorageEntity uploadUseCase = mock(UploadStorageEntity.class);
        when(beanFactory.getBean(eq(UploadStorageEntity.class), any())).thenReturn(uploadUseCase);
        when(ongoingGateways.get(anyString())).thenReturn(mock(MinioGatewayImpl.class));

        ByteArrayInputStream bais = new ByteArrayInputStream("data".getBytes());

        StorageEntityDto entity = StorageFile.builder()
                .name("file")
                .path("/file")
                .location("internal")
                .build();

        defaultStorageService.uploadTo(location, entity, bais);

        verify(uploadUseCase, times(1)).upload(entity, bais);
    }

    @Test
    public void testDelete() {
        String location = "internal";
        String path = "/path/to/file";

        StorageEntity mockEntity = mock(StorageEntity.class);
        when(mockEntity.getName()).thenReturn("file");
        when(mockEntity.getPath()).thenReturn(path);
        when(mockEntity.getContentType()).thenReturn("plain/text");
        when(mockEntity.getLastModified()).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(mockEntity.getSize()).thenReturn(15L);

        Collection<StorageEntity> entities = new ArrayList<>();
        entities.add(mockEntity);

        ListStorageEntity listUseCase = mock(ListStorageEntity.class);
        when(listUseCase.list()).thenReturn(entities);
        when(beanFactory.getBean(eq(ListStorageEntity.class), any())).thenReturn(listUseCase);

        DeleteStorageEntity deleteUseCase = mock(DeleteStorageEntity.class);
        when(beanFactory.getBean(eq(DeleteStorageEntity.class), any())).thenReturn(deleteUseCase);

        StorageEntityDto entity = mock(StorageEntityDto.class);
        when(entity.getLocation()).thenReturn(location);
        when(entity.getPath()).thenReturn(path);

        defaultStorageService.deleteFrom(location, path);

        verify(deleteUseCase, times(1)).delete(any());
    }

    @Test
    public void testDeleteWhenEntityNotFound() {
        String location = "internal";
        String path = "/path/to/name";

        ListStorageEntity listUseCase = mock(ListStorageEntity.class);
        when(beanFactory.getBean(eq(ListStorageEntity.class), any())).thenReturn(listUseCase);

        when(listUseCase.list()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> defaultStorageService.deleteFrom(location, path)
        );

        Assertions.assertEquals("Could not find entity: " + path, exception.getCause().getMessage());
        Assertions.assertInstanceOf(RuntimeException.class, exception.getCause());
    }

    @Test
    public void testDownload() {
        String location = "internal";
        String path = "/path/to/file";
        byte[] bytes = "some text".getBytes();

        StorageEntity mockEntity = mock(StorageEntity.class);
        when(mockEntity.getName()).thenReturn("file");
        when(mockEntity.getPath()).thenReturn(path);
        when(mockEntity.getContentType()).thenReturn("plain/text");
        when(mockEntity.getLastModified()).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(mockEntity.getSize()).thenReturn((long) bytes.length);
        when(mockEntity.getData()).thenReturn(bytes);

        Collection<StorageEntity> entities = new ArrayList<>();
        entities.add(mockEntity);

        ListStorageEntity listUseCase = mock(ListStorageEntity.class);
        when(listUseCase.list()).thenReturn(entities);
        when(beanFactory.getBean(eq(ListStorageEntity.class), any())).thenReturn(listUseCase);

        DownloadStorageEntity downloadUseCase = mock(DownloadStorageEntity.class);
        when(beanFactory.getBean(eq(DownloadStorageEntity.class), any())).thenReturn(downloadUseCase);

        StorageEntityDto entity = mock(StorageEntityDto.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(bytes, 0, bytes.length);

        when(downloadUseCase.download(any())).thenReturn(baos);
        when(entity.getLocation()).thenReturn(location);
        when(entity.getPath()).thenReturn(path);

        ByteArrayOutputStream result = defaultStorageService.download(entity.getLocation(), entity.getPath());

        assertEquals(baos, result);
        verify(downloadUseCase, times(1)).download(any());
    }

    @Test
    public void testDownloadWhenEntityNotFound() {
        String location = "internal";
        String path = "/path/to/name";

        ListStorageEntity listUseCase = mock(ListStorageEntity.class);
        when(beanFactory.getBean(eq(ListStorageEntity.class), any())).thenReturn(listUseCase);

        when(listUseCase.list()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> defaultStorageService.download(location, path)
        );

        Assertions.assertEquals("Could not find entity: " + path, exception.getCause().getMessage());
        Assertions.assertInstanceOf(RuntimeException.class, exception.getCause());
    }
}