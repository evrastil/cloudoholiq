package org.cloudoholiq.catalog.model.property.group;

import java.util.List;
import java.util.UUID;

public class CategoryFilterGroup {
    List<UUID> categories;
    FilterGroup filterGroup;

    public CategoryFilterGroup() {
    }

    public CategoryFilterGroup(List<UUID> categories, FilterGroup filterGroup) {
        this.categories = categories;
        this.filterGroup = filterGroup;
    }

    public FilterGroup getFilterGroup() {
        return filterGroup;
    }

    public void setFilterGroup(FilterGroup filterGroup) {
        this.filterGroup = filterGroup;
    }

    public List<UUID> getCategories() {
        return categories;
    }

    public void setCategories(List<UUID> categories) {
        this.categories = categories;
    }
}
