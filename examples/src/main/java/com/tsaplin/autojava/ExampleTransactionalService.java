package com.tsaplin.autojava;

public class ExampleTransactionalService implements TransactionalServiceTrait {

    @Transactional
    public boolean transactionalMethod() {
        return true;
    }

    @Transactional
    public boolean transactionalMethodThrowing() {
        throw new RuntimeException();
    }

    public void regularMethod() {
    }
}
