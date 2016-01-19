package com.tsaplin.autojava;

import com.google.common.collect.Lists;
import com.tsaplin.autojava.trait.Trait;

import java.util.List;

@Trait
public class ConcreteObservable implements Observable {

    private final List<Observer> observers = Lists.newArrayList();

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Observable observable) {
        for (Observer observer : observers) {
            observer.onChange(observable);
        }
    }
}
