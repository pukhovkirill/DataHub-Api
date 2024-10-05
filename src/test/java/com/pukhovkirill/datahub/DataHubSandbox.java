package com.pukhovkirill.datahub;

import com.pukhovkirill.datahub.infrastructure.TestConfig;
import org.springframework.boot.SpringApplication;

public class DataHubSandbox {
    public static void main(String[] arg){
        SpringApplication
                .from(DataHubApplication::main)
                .with(TestConfig.class)
                .run(arg);
    }
}
