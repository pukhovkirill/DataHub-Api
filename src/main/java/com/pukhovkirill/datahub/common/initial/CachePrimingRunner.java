package com.pukhovkirill.datahub.common.initial;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.pukhovkirill.datahub.common.service.StorageIndexingService;

@Component
public class CachePrimingRunner implements CommandLineRunner {

    private final StorageIndexingService indexingService;

    public CachePrimingRunner(StorageIndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @Order(10)
    @Override
    public void run(String... args) {
        indexingService.indexing();
    }
}
