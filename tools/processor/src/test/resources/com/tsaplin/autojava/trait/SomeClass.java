package com.tsaplin.autojava.trait;

import com.tsaplin.autojava.trait.Trait;

@Trait
public class SomeClass {

    public void acceptsArguments(String arg1, int arg2) {
    }

    public int returnsValue() {
        return 0;
    }

    public void throwsException() throws Exception {
        throw new Exception("Some exception");
    }
}
