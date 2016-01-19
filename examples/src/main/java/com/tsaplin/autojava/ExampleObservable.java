package com.tsaplin.autojava;

public class ExampleObservable implements ConcreteObservableTrait {

    public void doSomething() {
        notifyObservers(this);
    }

    public void doSomethingElse() {
        notifyObservers(this);
    }
}
