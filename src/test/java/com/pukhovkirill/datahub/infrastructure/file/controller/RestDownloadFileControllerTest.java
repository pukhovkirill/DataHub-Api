package com.pukhovkirill.datahub.infrastructure.file.controller;

import java.io.ByteArrayOutputStream;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pukhovkirill.datahub.infrastructure.file.service.StorageService;
import com.pukhovkirill.datahub.infrastructure.file.exception.InvalidParamException;

public class RestDownloadFileControllerTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private RestDownloadFileController restDownloadFileController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.storageService = mock(StorageService.class);
        this.restDownloadFileController = new RestDownloadFileController(storageService);
    }

    @Test
    public void download() throws IOException {
        String validPath = "location:/file.txt";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("file content".getBytes());

        when(storageService.download(anyString(), anyString())).thenReturn(baos);

        ResponseEntity<Map<String, Object>> response = restDownloadFileController.download(validPath, -1, -1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals("file content".getBytes(), (byte[]) response.getBody().get("data"));
        verify(storageService, times(1)).download(anyString(), anyString());
    }

    @Test
    public void downloadWhenPathIsNull() {
        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restDownloadFileController.download(null, -1, -1)
        );

        Assertions.assertEquals("path is null", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    public void downloadWhenPathIsEmpty() {
        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restDownloadFileController.download("", -1, -1)
        );

        Assertions.assertEquals("path is empty", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    public void downloadWhenPathIsBlank() {
        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restDownloadFileController.download("   ", -1, -1)
        );

        Assertions.assertEquals("path is empty", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    public void downloadWhenPathIsInvalid() {
        String invalidPath = "invalid_path_format";

        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restDownloadFileController.download(invalidPath, -1, -1)
        );

        Assertions.assertEquals("path is invalid", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

}
