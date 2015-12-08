package org.cloudoholiq.catalog.model.property.group;

import org.cloudoholiq.catalog.model.Entity;

public abstract class BaseGroup extends Entity {

    private String name;

    protected BaseGroup() {
    }

    public BaseGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
