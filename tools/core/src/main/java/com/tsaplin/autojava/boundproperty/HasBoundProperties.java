package com.tsaplin.autojava.boundproperty;

import java.beans.PropertyChangeListener;

/**
 * Should be implemented by classes with bound properties.
 */
public interface HasBoundProperties {
    default void addPropertyChangeListener(PropertyChangeListener listener) {
        throw new UnsupportedOperationException("Stab!!!");
    }
    default void removePropertyChangeListener(PropertyChangeListener listener) {
        throw new UnsupportedOperationException("Stab!!!");
    }
}
