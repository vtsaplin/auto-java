package com.tsaplin.autojava.trait;

import java.lang.Exception;
import java.lang.String;

@Trait(
    impl = SomeClass.class
)
public interface SomeClassTrait {
  default void acceptsArguments(String arg1, int arg2) {
    throw new UnsupportedOperationException("Stab!!!");}

  default int returnsValue() {
    throw new UnsupportedOperationException("Stab!!!");}

  default void throwsException() throws Exception {
    throw new UnsupportedOperationException("Stab!!!");}
}
