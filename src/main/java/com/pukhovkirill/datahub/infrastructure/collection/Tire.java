package com.pukhovkirill.datahub.infrastructure.collection;

import java.util.Collection;

public interface Tire<T> {
    void add(T entity);
    Collection<T> findAll();
    void lazyErase(T name);
    Collection<T> findFuzzy(String name);
    void fill(Iterable<T> entities);
    void clear();

}
