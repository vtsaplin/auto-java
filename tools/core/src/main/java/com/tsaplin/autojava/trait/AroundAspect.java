package com.tsaplin.autojava.trait;

/**
 * Can be implemented by trait backing classes to intercept method calls.
 */
public interface AroundAspect {
    Object around(CallableMethod method, Object target, Object[] args) throws Throwable;
}
