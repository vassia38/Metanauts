package com.main.model;

import java.util.Objects;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public User(Long id, String firstName, String lastName){
        this(firstName,lastName);
        super.setId(id);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public String toString() {
        return "User{ ID=" + this.getId() + " " +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),getFirstName(), getLastName());
    }
}