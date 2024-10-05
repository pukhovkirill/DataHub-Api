package com.pukhovkirill.datahub.infrastructure;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.usecase.uploadStorageEntityCase.UploadStorageEntity;
import com.pukhovkirill.datahub.usecase.uploadStorageEntityCase.UploadStorageEntityImpl;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MinIOContainer;

@TestConfiguration
public class TestConfig {

    @Bean
    public MinIOContainer minIOContainer(){
        return new MinIOContainer("minio/minio:latest")
                .withUserName("bf24e339e96f0c056c1b685807c0ba6496d5a6f637f2")
                .withPassword("7341c0b12ef3faa77bfd9525918a325a18e1a40b9c6f7ff3a2497c23fc067a1f")
                .withEnv("MINIO_DEFAULT_BUCKETS", "storage");
    }

    @Bean
    public MinioClient minioClient(MinIOContainer minIOContainer){
        return MinioClient
                .builder()
                .endpoint(minIOContainer.getS3URL())
                .credentials(minIOContainer.getUserName(), minIOContainer.getPassword())
                .build();
    }

    @Bean
    public UploadStorageEntity minioUploadFileUseCase(@Qualifier("minioClient") StorageGateway gateway){
        return new UploadStorageEntityImpl(gateway);
    }
}
