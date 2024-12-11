package com.pukhovkirill.datahub.infrastructure.file.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pukhovkirill.datahub.util.StringHelper;
import com.pukhovkirill.datahub.infrastructure.file.dto.StorageFile;
import com.pukhovkirill.datahub.infrastructure.file.service.StorageService;
import com.pukhovkirill.datahub.infrastructure.file.exception.InvalidParamException;

@RestController
public class RestUploadFileController extends RestFileController{

    private final StorageService storageService;
    
    public RestUploadFileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Upload the file.\s
                            The file size is lesser or equals than the CHUNK_SIZE setting.\s
                            """,
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schemaProperties = {
                                            @SchemaProperty(name = "timestamp", schema = @Schema(implementation = Timestamp.class)),
                                            @SchemaProperty(name = "status", schema = @Schema(implementation = int.class)),
                                            @SchemaProperty(name = "message", schema = @Schema(defaultValue = "success", implementation = String.class))

                                    })
                    }
            ),
            @ApiResponse(
                    responseCode = "102",
                    description = """
                            Upload the file.\s
                            If the file size is greater than the CHUNK_SIZE settings
                            it will received in chunks.""",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schemaProperties = {
                                            @SchemaProperty(name = "timestamp", schema = @Schema(implementation = Timestamp.class)),
                                            @SchemaProperty(name = "status", schema = @Schema(implementation = int.class)),
                                            @SchemaProperty(name = "message", schema = @Schema(defaultValue = "processing", implementation = String.class))

                                    })
                    }
            )
    })
    @RequestMapping(value = "api/files", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("path") String path,
                                                      @RequestParam(name = "total", required=false, defaultValue="-1") int total,
                                                      @RequestParam(name = "chunk", required=false, defaultValue="-1") int chunk) throws IOException {

        if(file == null)
            throw new InvalidParamException("file is null", HttpStatus.INTERNAL_SERVER_ERROR);
        else if(file.isEmpty())
            throw new InvalidParamException("file is empty", HttpStatus.BAD_REQUEST);

        if(path == null)
            throw new InvalidParamException("path is null", HttpStatus.INTERNAL_SERVER_ERROR);
        else if(path.isEmpty() || path.isBlank())
            throw new InvalidParamException("path is empty", HttpStatus.BAD_REQUEST);
        else if (!pathIsValid(path))
            throw new InvalidParamException("path is invalid", HttpStatus.BAD_REQUEST);

        return total == -1 && chunk == -1
                ? defaultUpload(file, path)
                : chunkedUpload(file, path, total, chunk);
    }

    private ResponseEntity<Map<String, Object>> defaultUpload(MultipartFile file, String path) throws IOException {
        String location = path.split(":")[0];
        String filepath = path.split(":")[1];
        String filename = StringHelper.extractName(filepath);

        StorageFile storageFile = StorageFile.builder()
                .name(filename)
                .path(filepath)
                .contentType(URLConnection.guessContentTypeFromName(filename))
                .size(file.getSize())
                .lastModified(new Timestamp(System.currentTimeMillis()))
                .location(location)
                .build();

        this.storageService.uploadTo(location, storageFile, new ByteArrayInputStream(file.getBytes()));

        return ResponseEntity.ok().body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.OK.value(),
                "message", "success"));
    }

    private ResponseEntity<Map<String, Object>> chunkedUpload(MultipartFile file,
                                                              String path,
                                                              int total, int chunk) throws IOException {
        String location = path.split(":")[0];
        String filepath = path.split(":")[1];

        Path chunked = Paths.get(UPLOAD_TMP_PATH, StringHelper.extractName(filepath)+".part");

        if(chunk == 0 && total > 0){
            Files.deleteIfExists(chunked);
            Files.createFile(chunked);
            Files.write(chunked, file.getBytes(), StandardOpenOption.APPEND);
        }else if(chunk > 0 && total > 0){
            Files.write(chunked, file.getBytes(), StandardOpenOption.APPEND);

            if(chunk >= total - 1){
                String filename = StringHelper.extractName(filepath);
                long size = Files.size(chunked);

                StorageFile storageFile = StorageFile.builder()
                        .name(filename)
                        .path(filepath)
                        .contentType(URLConnection.guessContentTypeFromName(filename))
                        .size(size)
                        .lastModified(new Timestamp(System.currentTimeMillis()))
                        .location(location)
                        .build();

                this.storageService
                        .uploadTo(location, storageFile, new ByteArrayInputStream(Files.readAllBytes(chunked)));
                Files.deleteIfExists(chunked);

                return ResponseEntity.ok().body(Map.of(
                        "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                        "status", HttpStatus.OK.value(),
                        "message", "success"));
            }
        }else{
            throw new IllegalArgumentException("Invalid total or chunk value");
        }

        return ResponseEntity.status(HttpStatus.PROCESSING).body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.PROCESSING.value(),
                "message", "processing"));
    }
}
