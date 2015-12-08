package org.cloudoholiq.catalog.search;

/**
* Created by vrastil on 26.1.2015.
*/
public class Pagination {
    private Integer offset;
    private Integer limit;

    public Pagination() {
    }

    public Pagination(Integer offset, Integer limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
