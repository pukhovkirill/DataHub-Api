package com.pukhovkirill.datahub.infrastructure.file.controller;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pukhovkirill.datahub.util.StringHelper;
import com.pukhovkirill.datahub.infrastructure.file.service.StorageService;
import com.pukhovkirill.datahub.infrastructure.file.exception.InvalidParamException;

@RestController
public class RestDownloadFileController extends RestFileController {

    private final StorageService storageService;

    public RestDownloadFileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(value = "api/files", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> download(@RequestParam("path") String path,
                                                        @RequestParam(name = "total", required=false, defaultValue="-1") int total,
                                                        @RequestParam(name = "chunk", required=false, defaultValue="-1") int chunk) throws IOException {
        if(path == null)
            throw new InvalidParamException("path is null", HttpStatus.INTERNAL_SERVER_ERROR);
        else if(path.isEmpty() || path.isBlank())
            throw new InvalidParamException("path is empty", HttpStatus.BAD_REQUEST);
        else if (!pathIsValid(path))
            throw new InvalidParamException("path is invalid", HttpStatus.BAD_REQUEST);

        String location = path.split(":")[0];
        String filepath = path.split(":")[1];

        ByteArrayOutputStream baos = storageService.download(location, filepath);

        if((total == -1 && chunk == -1 ) && baos.size() > CHUNK_SIZE){
            total = createTempFile(baos, filepath);
        }

        return total > 0 ? chunkedDownload(filepath, total, chunk) : defaultDownload(baos);
    }

    private ResponseEntity<Map<String, Object>> defaultDownload(ByteArrayOutputStream baos) throws IOException {
        byte[] bytes = baos.toByteArray();
        baos.close();

        return ResponseEntity.ok().body(Map.of(
                "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                "status", HttpStatus.OK.value(),
                "data", bytes));
    }

    private ResponseEntity<Map<String, Object>> chunkedDownload(String path, int total, int chunk) throws IOException {
        Path chunked = Paths.get(UPLOAD_TMP_PATH, StringHelper.extractName(path) + ".part");

        chunk = chunk + 1;
        long offset = (long) chunk * CHUNK_SIZE;

        long fileSize = Files.size(chunked);

        byte[] chunkData;
        try(SeekableByteChannel channel = Files.newByteChannel(chunked, StandardOpenOption.READ)) {
            channel.position(offset);
            ByteBuffer buffer = ByteBuffer.allocate(CHUNK_SIZE);
            int bytesRead = channel.read(buffer);

            if (bytesRead == -1) {
                chunkData = new byte[0];
            } else {
                buffer.flip();
                chunkData = new byte[bytesRead];
                buffer.get(chunkData);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        HttpStatus status = (offset + CHUNK_SIZE >= fileSize) ? HttpStatus.OK : HttpStatus.PROCESSING;
        if(status == HttpStatus.OK){
            Files.deleteIfExists(chunked);
            return ResponseEntity.ok().body(Map.of(
                    "timestamp", new Timestamp(System.currentTimeMillis()).toString(),
                    "status", status.value(),
                    "data", chunkData));
        }else{
            return ResponseEntity.status(status).body(Map.of(
                    "timestamp", new Timestamp(System.currentTimeMillis()).toString(),
                    "status", status.value(),
                    "total", total,
                    "chunk", chunk,
                    "data", chunkData));
        }
    }


    private int createTempFile(ByteArrayOutputStream baos, String path) throws IOException {
        Path chunked = Paths.get(UPLOAD_TMP_PATH, StringHelper.extractName(path)+".part");

        Files.deleteIfExists(chunked);
        Files.createFile(chunked);
        Files.write(chunked, baos.toByteArray(), StandardOpenOption.APPEND);
        baos.close();

        return Math.ceilDiv(baos.size(), CHUNK_SIZE);
    }
}
