package com.pukhovkirill.datahub.common.initial;

import com.pukhovkirill.datahub.common.service.StorageIndexingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
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
