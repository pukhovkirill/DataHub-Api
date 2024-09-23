package com.pukhovkirill.datahub.infrastructure.schema;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;


public class StorageEntitySchema implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;

    private String name;

    private String path;

    private String contentType;

    private Timestamp lastModified;

    private long size;

    private byte[] data;

}
