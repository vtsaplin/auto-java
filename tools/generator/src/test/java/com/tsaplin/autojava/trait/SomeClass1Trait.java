package com.tsaplin.autojava.trait;

@Trait(impl = SomeClass1.class)
public interface SomeClass1Trait {

    default int sum(int val1, int val2) {
        throw new UnsupportedOperationException("Stab!!!");
    }
}
