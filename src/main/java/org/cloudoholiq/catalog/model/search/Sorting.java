package org.cloudoholiq.catalog.model.search;

import org.cloudoholiq.catalog.model.Entity;

/**
* Created by vrastil on 26.1.2015.
*/
public class Sorting extends Entity {
    private Sort sort;
    private String orderBy;
    private Type type;
    private String key;

    public Sorting() {
    }

    public Sorting(Sort sort, String orderBy, Type type, String key) {
        this.sort = sort;
        this.orderBy = orderBy;
        this.type = type;
        this.key = key;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String selfPath() {
        return "/api/sorting";
    }
}
