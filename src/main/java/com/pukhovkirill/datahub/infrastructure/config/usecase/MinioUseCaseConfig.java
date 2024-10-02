package com.pukhovkirill.datahub.infrastructure.config.usecase;

import com.pukhovkirill.datahub.entity.gateway.StorageGateway;
import com.pukhovkirill.datahub.usecase.deleteStorageEntityCase.DeleteStorageEntity;
import com.pukhovkirill.datahub.usecase.deleteStorageEntityCase.DeleteStorageEntityImpl;
import com.pukhovkirill.datahub.usecase.downloadStorageEntityCase.DownloadStorageEntity;
import com.pukhovkirill.datahub.usecase.downloadStorageEntityCase.DownloadStorageEntityImpl;
import com.pukhovkirill.datahub.usecase.uploadStorageEntityCase.UploadStorageEntity;
import com.pukhovkirill.datahub.usecase.uploadStorageEntityCase.UploadStorageEntityImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class MinioUseCaseConfig {
    @Bean
    @Scope("prototype")
    public UploadStorageEntity minioUploadFileUseCase(@Qualifier("minioGatewayImpl") StorageGateway gateway){
        return new UploadStorageEntityImpl(gateway);
    }

    @Bean
    @Scope("prototype")
    public DownloadStorageEntity minioDownloadFileUseCase(@Qualifier("minioGatewayImpl") StorageGateway gateway){
        return new DownloadStorageEntityImpl(gateway);
    }

    @Bean
    @Scope("prototype")
    public DeleteStorageEntity minioDeleteFileUseCase(@Qualifier("minioGatewayImpl") StorageGateway gateway){
        return new DeleteStorageEntityImpl(gateway);
    }


}
