package com.main;

import com.main.controller.Controller;
import com.main.model.User;
import com.main.utils.events.Event;
import com.main.utils.observer.Observer;

public class EventsController implements Observer {
    public void afterLoad(Controller serviceController, User user) {

    }

    @Override
    public void updateFriends(Event event) {
    }

    @Override
    public void updateRequests(Event event) {

    }

    @Override
    public void updateSolvedRequests(Event event) {
    }

    @Override
    public void updateUsers(Event event) {
    }

    @Override
    public void updateMessages(Event event) {
    }

    @Override
    public void updateGroups(Event event) {
    }

    @Override
    public void updateGroupMessages(Event event) {
    }
}
