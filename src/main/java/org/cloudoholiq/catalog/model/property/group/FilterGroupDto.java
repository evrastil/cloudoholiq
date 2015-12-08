package org.cloudoholiq.catalog.model.property.group;

import org.cloudoholiq.catalog.model.search.Filter;

import java.util.List;

/**
 * Created by vrastil on 26.5.2015.
 */
public class FilterGroupDto {
    List<Filter> items;
    FilterGroup filterGroup;

    public FilterGroupDto() {
    }

    public FilterGroupDto(List<Filter> items, FilterGroup filterGroup) {
        this.items = items;
        this.filterGroup = filterGroup;
    }

    public List<Filter> getItems() {
        return items;
    }

    public void setItems(List<Filter> items) {
        this.items = items;
    }

    public FilterGroup getFilterGroup() {
        return filterGroup;
    }

    public void setFilterGroup(FilterGroup filterGroup) {
        this.filterGroup = filterGroup;
    }
}
