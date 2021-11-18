package com.main.model;

import java.time.LocalDateTime;

public class FriendshipDTO {
    String friendLastName;
    String friendFirstName;
    LocalDateTime friendshipDate;

    public FriendshipDTO(String friendName, String friendUsername, LocalDateTime friendshipDate) {
        this.friendLastName = friendName;
        this.friendFirstName = friendUsername;
        this.friendshipDate = friendshipDate;
    }

    public String getFriendLastName() {
        return friendLastName;
    }

    public String getFriendFirstName() {
        return friendFirstName;
    }

    public LocalDateTime getFriendshipDate() {
        return friendshipDate;
    }

    public void setFriendFirstName(String friendName) {
        this.friendFirstName = friendName;
    }

    public void setFriendLastName(String friendUsername) {
        this.friendLastName = friendUsername;
    }

    public void setFriendshipDate(LocalDateTime friendshipDate) {
        this.friendshipDate = friendshipDate;
    }

    @Override
    public String toString() {
        return "FriendshipDTO{" +
                "friendLastName='" + friendLastName + '\'' +
                ", friendFirstName='" + friendFirstName + '\'' +
                ", friendshipDate=" + friendshipDate.toString() +
                '}';
    }
}
