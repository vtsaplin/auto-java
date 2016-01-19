package com.tsaplin.autojava.boundproperty;

public class ClassThatShouldNotBeTransformed {

    private String value;

    public String getValue() {
        return value;
    }

    @PropertySetter
    public void setValue(String value) {
        this.value = value;
    }
}
