package com.tsaplin.autojava;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Observable observable);
}
