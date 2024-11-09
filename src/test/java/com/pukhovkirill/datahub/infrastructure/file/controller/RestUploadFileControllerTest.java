package com.pukhovkirill.datahub.infrastructure.file.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.infrastructure.file.service.StorageService;

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

        ResponseEntity<Map<String, Object>> response = restUploadFileController.upload(file, validPath);

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
    void uploadWithReturnInternalServerErrorWhenFileIsNull() throws IOException {
        ResponseEntity<Map<String, Object>> response = restUploadFileController.upload(null, "location:file.txt");

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("file is null", response.getBody().get("message"));
    }

    @Test
    void uploadWithReturnBadRequestWhenFileIsEmpty() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = restUploadFileController.upload(file, "location:file.txt");

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("file is empty", response.getBody().get("message"));
    }

    @Test
    void uploadWithReturnInternalServerErrorWhenPathIsNull() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        ResponseEntity<Map<String, Object>> response = restUploadFileController.upload(file, null);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("path is null", response.getBody().get("message"));
    }

    @Test
    void uploadWithReturnBadRequestWhenPathIsEmpty() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        ResponseEntity<Map<String, Object>> response = restUploadFileController.upload(file, "");

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("path is empty", response.getBody().get("message"));
    }

    @Test
    void uploadWithReturnBadRequestWhenPathIsInvalid() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        ResponseEntity<Map<String, Object>> response = restUploadFileController.upload(file, "invalid_path_format");

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("path is invalid", response.getBody().get("message"));
    }
}