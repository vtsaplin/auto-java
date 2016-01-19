package com.tsaplin.autojava.trait;

import com.google.common.collect.Lists;

import java.util.List;

@Trait
public class SomeClass2 implements SomeClass2Trait, AroundAspect  {

    private List<String> calledMethodNames = Lists.newArrayList();

    @Override
    public void willThrowException() throws SomeException {
        throw new SomeException();
    }

    public List<String> getCalledMethodNames() {
        return calledMethodNames;
    }

    @Override
    public Object around(CallableMethod method, Object target, Object[] args) throws Throwable {
        calledMethodNames.add(method.getName());
        return method.call(target, args);
    }
}
