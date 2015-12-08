package org.cloudoholiq.catalog.model.user;

import org.cloudoholiq.catalog.model.Entity;

import java.util.List;

public class Group extends Entity {
    List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String selfPath() {
        return "/api/groups";
    }
}
