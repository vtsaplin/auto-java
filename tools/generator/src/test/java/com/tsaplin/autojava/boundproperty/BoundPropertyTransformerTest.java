package com.tsaplin.autojava.boundproperty;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.build.IClassTransformer;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BoundPropertyTransformerTest {

    private static final String PROPERTY_NAME = "value";
    private static final String OLD_VALUE = null;
    private static final String NEW_VALUE = "SomeValue";

    private IClassTransformer transformer = new BoundPropertyTransformer();

    @Test
    public void shouldTransformClasses() throws Exception {
        CtClass clz = ClassPool.getDefault().getCtClass(getClass().getPackage().getName() + ".ClassWithProperties");
        assertTrue(transformer.shouldTransform(clz));
    }

    @Test
    public void shouldNotTransformClasses() throws Exception {
        CtClass clz = ClassPool.getDefault().getCtClass(getClass().getPackage().getName() + ".ClassThatShouldNotBeTransformed");
        assertFalse(transformer.shouldTransform(clz));
    }

    @Test
    public void shouldApplyTransformations() throws Exception {
        CtClass clz = ClassPool.getDefault().getCtClass(getClass().getPackage().getName() + ".ClassWithProperties");
        transformer.applyTransformations(clz);

        InterfaceWithProperties obj = (InterfaceWithProperties) clz.toClass().newInstance();
        PropertyChangeListener listener = mock(PropertyChangeListener.class);
        obj.addPropertyChangeListener(listener);
        obj.setValue(NEW_VALUE);

        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(captor.capture());
        assertEquals(PROPERTY_NAME, captor.getValue().getPropertyName());
        assertEquals(OLD_VALUE, captor.getValue().getOldValue());
        assertEquals(NEW_VALUE, captor.getValue().getNewValue());
    }

}
