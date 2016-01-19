package com.tsaplin.autojava;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ExampleObservableTest {

    Observer observer = Mockito.mock(Observer.class);

    ExampleObservable observable = new ExampleObservable();

    @Before
    public void setUp() throws Exception {
        observable.addObserver(observer);
    }

    @Test
    public void testDoSomething() throws Exception {
        observable.doSomething();
        Mockito.verify(observer).onChange(observable);
    }

    @Test
    public void testDoSomethingElse() throws Exception {
        observable.doSomethingElse();
        Mockito.verify(observer).onChange(observable);
    }
}
