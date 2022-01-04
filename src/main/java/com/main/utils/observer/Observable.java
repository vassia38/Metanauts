package com.main.utils.observer;

import com.main.utils.events.Event;

public interface Observable{
    void notifyObservers(UpdateType updateType, Event event);
    void addObserver(Observer obs);
    void removeObserver(Observer obs);
}
