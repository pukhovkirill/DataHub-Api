package com.pukhovkirill.datahub.usecase.listingStorageEntityCase.search;

import java.util.Collection;
import java.util.LinkedList;

import lombok.Setter;

import com.pukhovkirill.datahub.usecase.cache.storageEntity.StorageEntitiesCache;
import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.FilterCriteria;
import com.pukhovkirill.datahub.usecase.listingStorageEntityCase.criteria.SearchCriteria;

public class StorageEntitySearchImpl implements StorageEntitySearch {

    private final TireNode root;

    @Setter
    private static class TireNode{
        private final TireNode[] children;
        private StorageEntityDto entity;

        public TireNode() {
            this.children = new TireNode[26];
            this.entity = null;
        }

        public TireNode(StorageEntityDto entity){
            this();
            this.entity = entity;
        }
    }

    private void add(TireNode node, StorageEntityDto entity){
        String path = entity.getPath();

        for(char ch : path.toCharArray()){
            if (node.children[ch - 'a'] == null){
                node.children[ch - 'a'] = new TireNode();
            }
            node = node.children[ch - 'a'];
        }
        node.entity = entity;
    }

    public Collection<StorageEntityDto> find(TireNode node, String path) {
        Collection<StorageEntityDto> entities = new LinkedList<>();
        for(char ch : path.toCharArray()){
            if(node.entity != null) entities.add(node.entity.clone());
            if (node.children[ch - 'a'] == null){
                return entities;
            }
            node = node.children[ch - 'a'];
        }

        return entities;
    }

    public StorageEntitySearchImpl(StorageEntitiesCache storage) {
        this.root = new TireNode();
        fillTire(storage.getAllFromCache());
    }

    public void fillTire(Iterable<StorageEntityDto> entities){
        for(StorageEntityDto entity : entities)
            add(root, entity);
    }

    @Override
    public Iterable<StorageEntityDto> Search(SearchCriteria criteria) {
        return null;
    }

    @Override
    public StorageEntityDto FindById() {
        return null;
    }

    @Override
    public Iterable<StorageEntityDto> FindByName(String name) {
        return null;
    }

    @Override
    public Iterable<StorageEntityDto> SearchWithFilters(SearchCriteria criteria, FilterCriteria filters) {
        return null;
    }
}
