package org.cloudoholiq.catalog.search;

import java.util.List;

public class EntityListCount<T> {
    List<T> list;
    long count;

    public EntityListCount withList(List<T> list){
        setList(list);
        return this;
    }

    public EntityListCount withCount(long count){
        setCount(count);
        return this;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
