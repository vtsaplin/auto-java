package com.tsaplin.autojava.trait;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

/**
 * Represents an intercepted method.
 */
public interface CallableMethod {
    String getName();
    Annotation[] getAnnotations();
    Parameter[] getParameters();
    Class<?> getReturnType();
    Object call(Object obj, Object[] args) throws InvocationTargetException;
}
