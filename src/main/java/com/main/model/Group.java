package com.main.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        this.idsMembers = idsMembers == null ? new ArrayList<>() : idsMembers;
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

    public void addMemberId(Long id) {
        this.idsMembers.add(id);
    }

    public Long deleteMemberId(Long id) {
        if(this.idsMembers.remove(id))
            return id;
        return null;
    }

    public String toString(){
        StringBuilder str = new StringBuilder(this.getId() + " " + this.name);
        for( Long id : this.getIdsMembers()) {
            str.append(" ").append(id);
        }
        return str.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Group other = (Group) o;
        return Objects.equals(this.id, other.id);
    }
}
