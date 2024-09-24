package com.pukhovkirill.datahub.usecase.cache;

public interface Cacheable<T> {
    long expireAt();
    T getObject();
}
