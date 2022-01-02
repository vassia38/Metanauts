package com.main.utils.observer;

public interface Observable{
    void notifyObservers(UpdateType type);
    void addObserver(Observer obs);
    void removeObserver(Observer obs);
}
