package com.tsaplin.autojava.boundproperty;

public class ClassWithProperties implements InterfaceWithProperties {

    private String value;

    @Override
    public String getValue() {
        return value;
    }

    @PropertySetter
    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
