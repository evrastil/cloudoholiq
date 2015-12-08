package org.cloudoholiq.catalog.model.property.group;


import org.cloudoholiq.catalog.model.Entity;

public class FilterGroup extends Entity {

    String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String selfPath() {
        return "/api/filters/";
    }
}
