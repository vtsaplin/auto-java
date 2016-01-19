package com.tsaplin.autojava.trait;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.build.IClassTransformer;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TraitTransformerTest {

    static Class generatedClass;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        IClassTransformer transformer = new TraitTransformer();
        CtClass generatedCtClass = ClassPool.getDefault().getCtClass(TraitTransformerTest.class.getPackage().getName() + ".SomeOtherClass");
        transformer.applyTransformations(generatedCtClass);
        generatedClass = generatedCtClass.toClass();
    }

    @Test
    public void shouldAcceptArgumentsAndReturnValue() throws Exception {
        assertEquals(3, makeClassInstance().sum(1, 2));
    }

    @Test(expected = SomeException.class)
    public void shouldThrowException() throws Exception {
        ((SomeOtherClass) makeClassInstance()).willThrowException();
    }

    @Test
    public void shouldInterceptMethodCalls() throws Exception {
        SomeOtherClass obj = (SomeOtherClass) makeClassInstance();
        assertEquals(17, obj.get17());
        assertTrue(obj.getCalledMethodNames().contains("get17"));
    }

    @SuppressWarnings("unchecked")
    public <T extends SomeClass1Trait & SomeClass2Trait> T makeClassInstance() throws Exception {
        return (T) generatedClass.newInstance();
    }
}
