package com.main.model;

import java.util.Objects;

public class User extends Entity<Long> {
    private String username;
    private String firstName;
    private String lastName;
    private String userPassword;

    public User(String username, String firstName, String lastName, String userPassword) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userPassword = userPassword;
    }
    public User(Long id, String username, String firstName, String lastName, String userPassword){
        this(username, firstName, lastName, userPassword);
        super.setId(id);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "User{ username = '" + username + '\'' +
                ", firstName = ' " + firstName + '\'' +
                ", lastName = '" + lastName + '\'' +
                " }";
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
        User other = (User) o;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getFirstName(), getLastName(), getUserPassword());
    }
}