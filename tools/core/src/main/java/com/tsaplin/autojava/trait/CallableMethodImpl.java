package com.tsaplin.autojava.trait;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class CallableMethodImpl implements CallableMethod {

    private final String name;
    private final Method method;

    public CallableMethodImpl(String name, Method method) {
        this.name = name;
        this.method = method;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Annotation[] getAnnotations() {
        return method.getDeclaredAnnotations();
    }

    @Override
    public Parameter[] getParameters() {
        return method.getParameters();
    }

    @Override
    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    @Override
    public Object call(Object target, Object[] args) throws InvocationTargetException {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw  new RuntimeException("Error while calling intercepted method", e);
        }
    }
}
