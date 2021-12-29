package com.main.observer;

import java.util.ArrayList;
import java.util.List;

public class Observable {
    List<Observer> observers = new ArrayList<>();

    protected void notifyObservers(){
        for(Observer o : observers){
            o.update();
        }
    }

    public void addObserver(Observer obs){
        this.observers.add(obs);
    }

    public void removeObserver(Observer obs){
        this.observers.remove(obs);
    }
}
