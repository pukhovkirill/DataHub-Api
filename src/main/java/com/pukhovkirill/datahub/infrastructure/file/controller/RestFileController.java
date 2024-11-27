package com.pukhovkirill.datahub.infrastructure.file.controller;

import org.springframework.beans.factory.annotation.Value;

public abstract class RestFileController {

    @Value("${application.upload.tmp.dir}")
    protected String UPLOAD_TMP_PATH;

    @Value("${application.request.chunk.size}")
    protected int CHUNK_SIZE;

    protected boolean pathIsValid(String path) {
        if(!path.contains(":"))
            return false;

        if(path.startsWith(":") || path.endsWith(":"))
            return false;

        return path.indexOf(":") == path.lastIndexOf(":");
    }

}
