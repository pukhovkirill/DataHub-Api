package com.pukhovkirill.datahub.infrastructure.config.service;

import java.util.concurrent.TimeUnit;
import io.minio.MinioClient;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.Configuration;

import com.pukhovkirill.datahub.entity.factory.StorageEntityFactory;
import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.infrastructure.gateway.repository.MinioGatewayImpl;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String url;

    @Value("${minio.port}")
    private int port;

    @Value("${minio.secure}")
    private boolean secure;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.pool.size}")
    private int poolSize;

    @Value("${minio.pool.keep-alive}")
    private int keepAlive;

    @Value("${minio.pool.keep-alive-time-unit}")
    private TimeUnit timeUnit;

    @Bean
    @Scope("singleton")
    public MinioClient minioClient() {
        ConnectionPool connectionPool = new ConnectionPool(poolSize, keepAlive, timeUnit);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .build();

        return MinioClient.builder()
                .endpoint(url, port, secure)
                .credentials(accessKey, secretKey)
                .httpClient(okHttpClient)
                .build();
    }

    @Bean
    @Scope("prototype")
    public StorageGateway minioStorageGateway(MinioClient client, StorageEntityFactory factory) {
        return new MinioGatewayImpl(client, factory);
    }

}
