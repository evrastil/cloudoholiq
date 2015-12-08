package org.cloudoholiq.catalog.model.user;

import org.cloudoholiq.catalog.model.Entity;
import org.springframework.security.core.GrantedAuthority;

public class Role extends Entity implements GrantedAuthority {
    String name;
    @Override
    public String getAuthority() {
        return getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String selfPath() {
        return "/api/roles";
    }
}
