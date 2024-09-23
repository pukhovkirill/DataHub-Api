package com.pukhovkirill.datahub.entity.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class StorageEntity {

    private String name;

    private String path;

    private String contentType;

    private Timestamp lastModified;

    private long size;

    private byte[] data;

}
