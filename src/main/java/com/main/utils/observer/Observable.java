package com.main.utils.observer;

public interface Observable{
    void notifyObservers(UpdateType updateType, OperationType operationType);
    void addObserver(Observer obs);
    void removeObserver(Observer obs);
}
