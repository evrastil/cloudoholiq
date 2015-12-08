package org.cloudoholiq.catalog.model.common;


import javax.validation.constraints.Size;
import java.util.UUID;

public class Blob extends BlobMetadata {
    //2MB
    @Size(max = 2097152)
    byte[] data;

    public Blob() {
    }

    public Blob(String name, long size, String mediaType, String collection, String entityType, UUID entityId, byte[] data) {
        super(name, size, mediaType, collection, entityType, entityId);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


}
