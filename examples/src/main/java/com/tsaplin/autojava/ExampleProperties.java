package com.tsaplin.autojava;

import com.tsaplin.autojava.boundproperty.HasBoundProperties;
import com.tsaplin.autojava.boundproperty.PropertySetter;

public class ExampleProperties implements HasBoundProperties {

    private int width;
    private int height;

    public ExampleProperties(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    @PropertySetter
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    @PropertySetter
    public void setHeight(int height) {
        this.height = height;
    }
}
