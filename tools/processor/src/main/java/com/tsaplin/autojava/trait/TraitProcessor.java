package com.tsaplin.autojava.trait;

import com.squareup.javapoet.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("com.tsaplin.autojava.trait.Trait")
public class TraitProcessor extends AbstractProcessor {

    private static final String INTERFACE_SUFFIX = Trait.class.getSimpleName();

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
        try {
            for (TypeElement t : typeElements) {
                for (Element e : roundEnvironment.getElementsAnnotatedWith(t)) {
                    if (e.getKind() == ElementKind.CLASS) {

                        TypeElement classElement = (TypeElement) e;
                        PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();

                        TypeSpec.Builder typeBuilder = TypeSpec.interfaceBuilder(classElement.getSimpleName() + INTERFACE_SUFFIX)
                                .addModifiers(Modifier.PUBLIC)
                                .addAnnotation(AnnotationSpec.builder(Trait.class).addMember("impl", "$T.class", ClassName.get(classElement)).build());

                        for (TypeMirror type : classElement.getInterfaces()) {
                            if (!type.toString().equals(AroundAspect.class.getName())) {
                                typeBuilder.addSuperinterface(TypeName.get(type));
                            }
                        }

                        ElementFilter.methodsIn(classElement.getEnclosedElements()).forEach(executableElement -> {

                            if (executableElement.getKind() == ElementKind.METHOD
                                    && !executableElement.getModifiers().contains(Modifier.PRIVATE)
                                    && !executableElement.getSimpleName().toString().equals("around")) {
                                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                                        .addExceptions(executableElement.getThrownTypes().stream().map(type -> TypeName.get(type)).collect(Collectors.toList()))
                                        .returns(TypeName.get(executableElement.getReturnType())).addModifiers(Modifier.DEFAULT, Modifier.PUBLIC);
                                for (VariableElement variableElement : executableElement.getParameters()) {
                                    methodBuilder.addParameter(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString());
                                }
                                methodBuilder.addCode("throw new UnsupportedOperationException(\"Stab!!!\");");
                                typeBuilder.addMethod(methodBuilder.build());
                            }
                        });

                        JavaFile javaFile = JavaFile.builder(packageElement.getQualifiedName().toString(), typeBuilder.build()).build();
                        javaFile.writeTo(processingEnv.getFiler());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
