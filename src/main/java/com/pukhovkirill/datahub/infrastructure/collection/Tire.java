package com.pukhovkirill.datahub.infrastructure.collection;

import java.util.Collection;

public interface Tire<T> {
    void add(T entity);
    T find(String name);
    void lazyErase(T name);
    Collection<T> findFuzzy(String name);
    void fill(Iterable<T> entities);

}
