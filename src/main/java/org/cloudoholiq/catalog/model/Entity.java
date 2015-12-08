package org.cloudoholiq.catalog.model;

import org.cloudoholiq.catalog.common.repository.IEntity;

import java.util.UUID;

public abstract class Entity implements IEntity {

    int timestamp;

//    private Map<String, Object> _links = new LinkedHashMap<>();
//
//    @JsonProperty(value = "_links")
//    public Map getLinks() {
//        _links.put("_href", new LinkedHashMap<String, String>() {{
//            put("self", String.format("%s/%s", selfPath(), getId()));
//        }});
//        return _links;
//    }

    UUID id;

    public Entity() {
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
//        String.format("%s/%s",selfPath(),id);
    }
    public abstract String selfPath();

}
