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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.infrastructure.file.service.StorageService;
import com.pukhovkirill.datahub.infrastructure.file.exception.InvalidParamException;

class RestUploadFileControllerTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private RestUploadFileController restUploadFileController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.storageService = mock(StorageService.class);
        this.restUploadFileController = new RestUploadFileController(storageService);
        ReflectionTestUtils.setField(restUploadFileController, "CHUNK_SIZE", 52428800);
        ReflectionTestUtils.setField(restUploadFileController, "UPLOAD_TMP_PATH", "/tmp/");
    }


    /* NOT CHUNKED UPLOAD TESTS */
    @Test
    public void testUpload() throws IOException {
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
    public void testUploadWhenFileIsNull() {
        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restUploadFileController.upload(null, "location:file.txt", -1, -1)
        );

        Assertions.assertEquals("file is null", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    public void testUploadWhenFileIsEmpty() {
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
    public void testUploadWhenPathIsNull() {
        MultipartFile file = mock(MultipartFile.class);

        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restUploadFileController.upload(file, null, -1, -1)
        );

        Assertions.assertEquals("path is null", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    public void testUploadWhenPathIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);

        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restUploadFileController.upload(file, "", -1, -1)
        );

        Assertions.assertEquals("path is empty", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }

    @Test
    public void testUploadWhenPathIsInvalid() {
        MultipartFile file = mock(MultipartFile.class);
        String invalidPath = "invalid_path_format";

        RuntimeException exception = assertThrows(
                InvalidParamException.class,
                () -> restUploadFileController.upload(file, invalidPath, -1, -1)
        );

        Assertions.assertEquals("path is invalid", exception.getMessage());
        Assertions.assertInstanceOf(InvalidParamException.class, exception);
    }


    /* CHUNKED UPLOAD TESTS */
    /* ALL TESTS USE TWO STEP CHUNKED REQUESTS */
    @Test
    public void testChunkedUpload() throws IOException {
        String validPath = "internal:test_file.pdf";


        /* FIRST CHUNK TO SERVER */

        byte[] part1 = generateResults(1);

        MultipartFile multipartFile1 = mock(MultipartFile.class);
        when(multipartFile1.isEmpty()).thenReturn(false);
        when(multipartFile1.getSize()).thenReturn((long) part1.length);
        when(multipartFile1.getBytes()).thenReturn(part1);

        int total = 2;
        int chunk = 0;


        ResponseEntity<Map<String, Object>> response1 =
                restUploadFileController.upload(multipartFile1, validPath, total, chunk);


        assertNotNull(response1);
        assertEquals(HttpStatus.PROCESSING, response1.getStatusCode());


        /* SECOND CHUNK TO SERVER */

        byte[] part2 = generateResults(2);

        MultipartFile multipartFile2 = mock(MultipartFile.class);
        when(multipartFile2.isEmpty()).thenReturn(false);
        when(multipartFile2.getSize()).thenReturn((long) part2.length);
        when(multipartFile2.getBytes()).thenReturn(part2);

        chunk = chunk+1;


        ResponseEntity<Map<String, Object>> response2 =
                restUploadFileController.upload(multipartFile2, validPath, total, chunk);


        assertNotNull(response2);
        assertEquals(HttpStatus.OK, response2.getStatusCode());


        verify(storageService, times(1))
                .uploadTo(anyString(), any(StorageEntityDto.class), any(ByteArrayInputStream.class));

        ArgumentCaptor<ByteArrayInputStream> argumentCaptor = ArgumentCaptor.forClass(ByteArrayInputStream.class);
        verify(storageService).uploadTo(anyString(), any(StorageEntityDto.class), argumentCaptor.capture());

        byte[] actual = argumentCaptor.getValue().readAllBytes();
        byte[] excepted = generateValidChunkedDownloadTestCase().toByteArray();


        assertArrayEquals(excepted, actual);
    }

    @Test
    public void testChunkedUploadWhenChunkLessThanMinusOne() throws IOException {
        String validPath = "internal:test_file.pdf";


        /* FIRST CHUNK TO SERVER */

        byte[] part1 = generateResults(1);

        MultipartFile multipartFile1 = mock(MultipartFile.class);
        when(multipartFile1.isEmpty()).thenReturn(false);
        when(multipartFile1.getSize()).thenReturn((long) part1.length);
        when(multipartFile1.getBytes()).thenReturn(part1);

        int total = 2;
        int chunk = -2;

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> restUploadFileController.upload(multipartFile1, validPath, total, chunk)
        );

        Assertions.assertNotNull(exception);
        Assertions.assertInstanceOf(IllegalArgumentException.class, exception);
        Assertions.assertEquals("Invalid total or chunk value", exception.getMessage());
    }

    @Test
    public void testChunkedUploadWhenChunkMoreThanTotal() throws IOException {
        String validPath = "internal:test_file.pdf";


        /* FIRST CHUNK TO SERVER */

        byte[] part1 = generateResults(1);

        MultipartFile multipartFile1 = mock(MultipartFile.class);
        when(multipartFile1.isEmpty()).thenReturn(false);
        when(multipartFile1.getSize()).thenReturn((long) part1.length);
        when(multipartFile1.getBytes()).thenReturn(part1);

        int total = 2;
        int chunk = 0;


        ResponseEntity<Map<String, Object>> response1 =
                restUploadFileController.upload(multipartFile1, validPath, total, chunk);


        assertNotNull(response1);
        assertEquals(HttpStatus.PROCESSING, response1.getStatusCode());


        /* SECOND CHUNK TO SERVER */

        byte[] part2 = generateResults(2);

        MultipartFile multipartFile2 = mock(MultipartFile.class);
        when(multipartFile2.isEmpty()).thenReturn(false);
        when(multipartFile2.getSize()).thenReturn((long) part2.length);
        when(multipartFile2.getBytes()).thenReturn(part2);

        chunk = chunk+2;


        ResponseEntity<Map<String, Object>> response2 =
                restUploadFileController.upload(multipartFile2, validPath, total, chunk);


        assertNotNull(response2);
        assertEquals(HttpStatus.OK, response2.getStatusCode());


        verify(storageService, times(1))
                .uploadTo(anyString(), any(StorageEntityDto.class), any(ByteArrayInputStream.class));

        ArgumentCaptor<ByteArrayInputStream> argumentCaptor = ArgumentCaptor.forClass(ByteArrayInputStream.class);
        verify(storageService).uploadTo(anyString(), any(StorageEntityDto.class), argumentCaptor.capture());

        byte[] actual = argumentCaptor.getValue().readAllBytes();
        byte[] excepted = generateValidChunkedDownloadTestCase().toByteArray();


        assertArrayEquals(excepted, actual);
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