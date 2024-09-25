package com.pukhovkirill.datahub.infrastructure.collection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

public class StorageEntityArrayTire implements Tire<StorageEntityDto>{

    private volatile TireNode root;

    private final Lock writeLock = new ReentrantLock();

    public StorageEntityArrayTire() {
        root = new TireNode();
    }

    private static class TireNode {
        private final AtomicReferenceArray<TireNode> outgoingNodes;

        private volatile StorageEntityDto entity;

        public TireNode() {
            this.outgoingNodes = new AtomicReferenceArray<>(new TireNode[26]);
            this.entity = null;
        }

        public TireNode getChild(char ch){
            return this.outgoingNodes.get(ch - 'a');
        }

        public synchronized void setChild(char ch){
            this.outgoingNodes.set(ch - 'a', new TireNode());
        }

        public StorageEntityDto getEntity(){
            return this.entity;
        }

        public synchronized void setEntity(StorageEntityDto entity){
            if(entity == null) return;
            this.entity = entity.clone();
        }
    }

    protected void acquireWriteLock() {
        writeLock.lock();
    }

    protected void releaseWriteLock() {
        writeLock.unlock();
    }

    @Override
    public void add(StorageEntityDto entity) {
        acquireWriteLock();
        try{
            String path = entity.getPath();
            var currentNode = this.root;
            for(char ch : path.toCharArray()){
                if (currentNode.getChild(ch) == null){
                    currentNode.setChild(ch);
                }
                currentNode = currentNode.getChild(ch);
            }
            currentNode.setEntity(entity);
        }finally{
            releaseWriteLock();
        }
    }

    @Override
    public StorageEntityDto find(String path) {
        var currentNode = this.root;
        for(char ch : path.toCharArray()){
            if (currentNode.getChild(ch) == null){
                return null;
            }
            currentNode = currentNode.getChild(ch);
        }

        return currentNode.getEntity().clone();
    }

    @Override
    public void lazyErase(String path) {
        var currentNode = this.root;
        for(char ch : path.toCharArray()){
            if (currentNode.getChild(ch) == null){
                return;
            }
            currentNode = currentNode.getChild(ch);
        }

        currentNode.setEntity(null);
    }

    @Override
    public Collection<StorageEntityDto> findFuzzy(String path) {
        Collection<StorageEntityDto> entities = new LinkedList<>();
        var currentNode = this.root;
        for(char ch : path.toCharArray()){
            if(currentNode.getChild(ch) != null) entities.add(currentNode.entity.clone());
            if (currentNode.getChild(ch) == null){
                return entities;
            }
            currentNode = currentNode.getChild(ch);
        }

        return entities;
    }

    @Override
    public void fill(Iterable<StorageEntityDto> entities){
        acquireWriteLock();
        try{
            for(StorageEntityDto entity : entities){
                var currentNode = this.root;
                String path = entity.getPath();
                for(char ch : path.toCharArray()){
                    if (currentNode.getChild(ch) == null){
                        currentNode.setChild(ch);
                    }
                    currentNode = currentNode.getChild(ch);
                }
                currentNode.setEntity(entity);
            }
        }finally{
            releaseWriteLock();
        }
    }
}
