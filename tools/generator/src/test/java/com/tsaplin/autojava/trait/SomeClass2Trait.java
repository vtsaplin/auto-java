package com.tsaplin.autojava.trait;

import java.util.List;

@Trait(impl = SomeClass2.class)
public interface SomeClass2Trait {

    default void willThrowException() throws Exception {
        throw new UnsupportedOperationException("Stab!!!");
    }

    default List<String> getCalledMethodNames() throws Exception {
        throw new UnsupportedOperationException("Stab!!!");
    }
}
