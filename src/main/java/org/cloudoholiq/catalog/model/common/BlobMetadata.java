package org.cloudoholiq.catalog.model.common;

import org.cloudoholiq.catalog.model.Entity;

import java.util.UUID;

/**
 * Created by vrastil on 30.3.2015.
 */
public class BlobMetadata extends Entity {
    String name;
    long size;
    String mediaType;
    String collection;
    String entityType;
    UUID entityId;

    public BlobMetadata() {
    }

    public BlobMetadata(String name, long size, String mediaType, String collection, String entityType, UUID entityId) {
        this.name = name;
        this.size = size;
        this.mediaType = mediaType;
        this.collection = collection;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Override
    public String selfPath() {
        return "/api/blob-store";
    }
}
