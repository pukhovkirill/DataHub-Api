package com.pukhovkirill.datahub.infrastructure.file.controller;

public abstract class RestFileController {

    protected boolean pathIsValid(String path) {
        if(!path.contains(":"))
            return false;

        if(path.startsWith(":") || path.endsWith(":"))
            return false;

        return path.indexOf(":") == path.lastIndexOf(":");
    }

}
