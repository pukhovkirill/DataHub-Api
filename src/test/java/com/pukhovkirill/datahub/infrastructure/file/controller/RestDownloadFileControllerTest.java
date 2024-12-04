package com.pukhovkirill.datahub.infrastructure.file.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

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
        ReflectionTestUtils.setField(restDownloadFileController, "CHUNK_SIZE", 52428800);
        ReflectionTestUtils.setField(restDownloadFileController, "UPLOAD_TMP_PATH", "/tmp/");
    }


    /* NOT CHUNKED DOWNLOAD TESTS */
    @Test
    public void testDownload() throws IOException {
        String validPath = "location:file.txt";
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
    public void testDownloadWhenPathIsNull() {
        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restDownloadFileController.download(null, -1, -1)
        );

        Assertions.assertEquals("path is null", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    public void testDownloadWhenPathIsEmpty() {
        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restDownloadFileController.download("", -1, -1)
        );

        Assertions.assertEquals("path is empty", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    public void testDownloadWhenPathIsBlank() {
        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restDownloadFileController.download("   ", -1, -1)
        );

        Assertions.assertEquals("path is empty", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    public void testDownloadWhenPathIsInvalid() {
        String invalidPath = "invalid_path_format";

        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restDownloadFileController.download(invalidPath, -1, -1)
        );

        Assertions.assertEquals("path is invalid", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }


    /* CHUNKED DOWNLOAD TESTS */
    /* ALL TESTS USE TWO STEP CHUNKED REQUESTS */
    @Test
    public void testChunkedDownload() throws IOException {
        String validPath = "internal:test_file.pdf";
        ByteArrayOutputStream baos = generateValidChunkedDownloadTestCase();
        when(storageService.download(anyString(), anyString())).thenReturn(baos);

        byte[] actual;
        byte[] excepted;


        /* FIRST CHUNK FROM SERVER */

        int total = -1;
        int chunk = -1;

        ResponseEntity<Map<String, Object>> response1 = restDownloadFileController.download(validPath, total, chunk);

        assertNotNull(response1);
        assertEquals(HttpStatus.PROCESSING, response1.getStatusCode());
        assertNotNull(response1.getBody());

        actual = (byte[]) response1.getBody().get("data");
        excepted = generateResults(1);

        assertArrayEquals(excepted, actual);


        /* SECOND CHUNK FROM SERVER */

        total = (int) response1.getBody().getOrDefault("total", -1);
        chunk = (int) response1.getBody().getOrDefault("chunk", -1);

        ResponseEntity<Map<String, Object>> response2 = restDownloadFileController.download(validPath, total, chunk);

        assertNotNull(response2);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertNotNull(response2.getBody());

        actual = (byte[]) response2.getBody().get("data");
        excepted = generateResults(2);

        assertArrayEquals(excepted, actual);
    }

    @Test
    public void testChunkedDownloadWhenChunkLessThanMinusOne() throws IOException {
        String validPath = "internal:test_file.pdf";
        ByteArrayOutputStream baos = generateValidChunkedDownloadTestCase();
        when(storageService.download(anyString(), anyString())).thenReturn(baos);

        byte[] actual;
        byte[] excepted;


        /* FIRST CHUNK FROM SERVER */

        int total = -1;
        int chunk = -1;

        ResponseEntity<Map<String, Object>> response1 = restDownloadFileController.download(validPath, total, chunk);

        assertNotNull(response1);
        assertEquals(HttpStatus.PROCESSING, response1.getStatusCode());
        assertNotNull(response1.getBody());

        actual = (byte[]) response1.getBody().get("data");
        excepted = generateResults(1);

        assertArrayEquals(excepted, actual);


        /* SECOND CHUNK FROM SERVER */

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> restDownloadFileController.download(validPath, 1, -2)
        );

        assertNotNull(exception);
        assertNotNull(exception.getCause());
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }

    @Test
    public void testChunkedDownloadWhenChunkMoreThanTotal() throws IOException {
        String validPath = "internal:test_file.pdf";
        ByteArrayOutputStream baos = generateValidChunkedDownloadTestCase();
        when(storageService.download(anyString(), anyString())).thenReturn(baos);

        byte[] actual;
        byte[] excepted;


        /* FIRST CHUNK FROM SERVER */

        int total = -1;
        int chunk = -1;

        ResponseEntity<Map<String, Object>> response1 = restDownloadFileController.download(validPath, total, chunk);

        assertNotNull(response1);
        assertEquals(HttpStatus.PROCESSING, response1.getStatusCode());
        assertNotNull(response1.getBody());

        actual = (byte[]) response1.getBody().get("data");
        excepted = generateResults(1);

        assertArrayEquals(excepted, actual);


        /* SECOND CHUNK FROM SERVER */

        total = (int) response1.getBody().getOrDefault("total", -1);
        chunk = 3;

        ResponseEntity<Map<String, Object>> response2 = restDownloadFileController.download(validPath, total, chunk);

        assertNotNull(response2);
        assertNotEquals(HttpStatus.PROCESSING, response2.getStatusCode());
        assertNotNull(response2.getBody());

        actual = (byte[]) response2.getBody().get("data");

        assertArrayEquals(new byte[0], actual);
    }

    private ByteArrayOutputStream generateValidChunkedDownloadTestCase() throws IOException {
        return readFile("src", "test", "java", "resources", "test_file.pdf");
    }

    private byte[] generateResults(int part) throws IOException {
        return readFile("src", "test", "java", "resources", "test_file.part" + part).toByteArray();
    }

    private ByteArrayOutputStream readFile(String first, String... more) throws IOException {
        Path path = Paths.get(first, more);
        ByteArrayInputStream bais = new ByteArrayInputStream(Files.readAllBytes(path));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int count;
        byte[] buf = new byte[1024];
        while((count = bais.read(buf)) != -1) {
            baos.write(buf, 0, count);
        }

        return baos;
    }
}
