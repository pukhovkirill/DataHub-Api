package com.pukhovkirill.datahub.usecase.searchStorageEntityCase.criteria.search;

public interface Query {
    void setValue(String query);
    String getValue();
}
