package com.pukhovkirill.datahub.infrastructure.file.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.infrastructure.file.service.StorageService;
import com.pukhovkirill.datahub.infrastructure.file.exception.InvalidParamException;

class RestUploadFileControllerTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private RestUploadFileController restUploadFileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.storageService = mock(StorageService.class);
        this.restUploadFileController = new RestUploadFileController(storageService);
    }

    @Test
    void upload() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        String validPath = "location:file.txt";
        when(file.isEmpty()).thenReturn(false);
        when(file.getBytes()).thenReturn("file content".getBytes());
        when(file.getSize()).thenReturn((long) "file content".getBytes().length);

        ResponseEntity<Map<String, Object>> response = restUploadFileController.upload(file, validPath, -1, -1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("message"));
        verify(storageService, times(1)).uploadTo(
                eq("location"),
                any(StorageFile.class),
                any(ByteArrayInputStream.class)
        );
    }

    @Test
    void uploadWhenFileIsNull() {
        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restUploadFileController.upload(null, "location:file.txt", -1, -1)
        );

        Assertions.assertEquals("file is null", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    void uploadWhenFileIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(true);

        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restUploadFileController.upload(file, "location:file.txt", -1, -1)
        );

        Assertions.assertEquals("file is empty", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    void uploadWhenPathIsNull() {
        MultipartFile file = mock(MultipartFile.class);

        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restUploadFileController.upload(file, null, -1, -1)
        );

        Assertions.assertEquals("path is null", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    void uploadWhenPathIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);

        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restUploadFileController.upload(file, "", -1, -1)
        );

        Assertions.assertEquals("path is empty", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    void uploadWhenPathIsInvalid() {
        MultipartFile file = mock(MultipartFile.class);
        String invalidPath = "invalid_path_format";

        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restUploadFileController.upload(file, invalidPath, -1, -1)
        );

        Assertions.assertEquals("path is invalid", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }
}