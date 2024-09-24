package com.pukhovkirill.datahub.infrastructure.cache;

public interface Cacheable<T> {
    long expireAt();
    T getObject();
}
