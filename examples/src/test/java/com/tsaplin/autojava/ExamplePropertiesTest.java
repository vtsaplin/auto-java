package com.tsaplin.autojava;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.junit.Assert.*;

public class ExamplePropertiesTest {

    PropertyChangeListener listener = Mockito.mock(PropertyChangeListener.class);
    ArgumentCaptor<PropertyChangeEvent> arguments = ArgumentCaptor.forClass(PropertyChangeEvent.class);

    ExampleProperties properties = new ExampleProperties(0, 0);

    @Before
    public void setUp() throws Exception {
        properties.addPropertyChangeListener(listener);
    }

    @Test
    public void testSetWidth() throws Exception {
        properties.setWidth(100);
        Mockito.verify(listener).propertyChange(arguments.capture());
        assertEquals(properties, arguments.getValue().getSource());
        assertEquals("width", arguments.getValue().getPropertyName());
        assertEquals(0, arguments.getValue().getOldValue());
        assertEquals(100, arguments.getValue().getNewValue());
    }

    @Test
    public void testSetHeight() throws Exception {
        properties.setHeight(100);
        Mockito.verify(listener).propertyChange(arguments.capture());
        assertEquals(properties, arguments.getValue().getSource());
        assertEquals("height", arguments.getValue().getPropertyName());
        assertEquals(0, arguments.getValue().getOldValue());
        assertEquals(100, arguments.getValue().getNewValue());
    }
}
