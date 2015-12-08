package org.cloudoholiq.catalog.search;

import org.cloudoholiq.catalog.model.search.Filter;
import org.cloudoholiq.catalog.model.search.Sorting;

import java.util.List;

public class SearchQueryParam {

    private Pagination pagination;
    private List<Filter> filters;
    private List<Sorting> sorting;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<Sorting> getSorting() {
        return sorting;
    }

    public void setSorting(List<Sorting> sorting) {
        this.sorting = sorting;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }
}
