package com.pukhovkirill.datahub.infrastructure.collection;

import java.util.Collection;

public interface Tire<T> {
    void add(T entity);
    T find(String path);
    void lazyErase(String path);
    Collection<T> findFuzzy(String path);
    void fill(Iterable<T> entities);

}
