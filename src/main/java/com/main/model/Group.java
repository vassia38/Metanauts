package com.main.model;

import java.util.ArrayList;
import java.util.List;

public class Group extends Entity<Long> {
    private String name;
    private final List<Long> idsMembers;

    public Group(String name) {
        this.name = name;
        this.idsMembers = new ArrayList<>();
    }

    public Group(Long id, String name) {
        this(name);
        this.setId(id);
    }

    public Group(String name, List<Long> idsMembers) {
        this.name = name;
        this.idsMembers = idsMembers;
    }

    public Group(Long id, String name, List<Long> idsMembers) {
        this(name, idsMembers);
        this.setId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getIdsMembers() {
        return this.idsMembers;
    }

    public String toString(){
        StringBuilder str = new StringBuilder(this.getId() + " " + this.name);
        for( Long id : this.getIdsMembers()) {
            str.append(" ").append(id);
        }
        return str.toString();
    }
}
