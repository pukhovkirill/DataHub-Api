package com.pukhovkirill.datahub.common.initial;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.pukhovkirill.datahub.common.service.StorageIndexingService;

@Order(2)
@Component
public class CachePrimingRunner implements CommandLineRunner {

    private final StorageIndexingService indexingService;

    public CachePrimingRunner(StorageIndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @Override
    public void run(String... args) {
        indexingService.indexing();
    }
}
