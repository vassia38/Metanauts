package com.main.utils.observer;

import com.main.utils.events.Event;

public interface Observer{

    void updateFriends(Event event);
    void updateRequests(Event event);
    void updateSolvedRequests(Event event);
    void updateUsers(Event event);
    void updateMessages(Event event);
    void updateGroups(Event event);
    void updateGroupMessages(Event event);
}
