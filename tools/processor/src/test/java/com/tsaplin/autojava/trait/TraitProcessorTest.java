package com.tsaplin.autojava.trait;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class TraitProcessorTest {

    @Test
    public void shouldGenerateTraitInterface() throws Exception {
        assertAbout(javaSource())
                .that(JavaFileObjects.forResource(getClass().getResource("SomeClass.java")))
                .processedWith(new TraitProcessor())
                .compilesWithoutError()
                .and().generatesSources(JavaFileObjects.forResource(getClass().getResource("SomeClassTrait.java")));
    }
}
