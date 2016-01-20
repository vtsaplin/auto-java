package com.tsaplin.autojava.trait;

import de.icongmbh.oss.maven.plugin.javassist.ClassTransformer;
import javassist.*;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.annotation.Annotation;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Transforms classes to support multiple inheritance via delegation.
 */
public class TraitTransformer extends ClassTransformer implements IClassTransformer {

    private static final Pattern TRAIT_CLASS_PATTERN = Pattern.compile("Trait\\(impl=(.*).class\\)");

    @Override
    public void applyTransformations(CtClass ctClass) throws JavassistBuildException {
        try {
            transform(ctClass);
        } catch (Exception e) {
            throw new JavassistBuildException(e);
        }
    }

    @Override
    public boolean shouldTransform(CtClass ctClass) throws JavassistBuildException {
        return !(ctClass.isFrozen() || ctClass.isInterface() || ctClass.isAnnotation() || ctClass.isArray() || ctClass.isPrimitive());
    }

    public void transform(CtClass ctClass) throws JavassistBuildException {
        try {
            CtClass aroundAspectClass = ctClass.getClassPool().get(AroundAspect.class.getName());

            Set<String> addedMethodSignatures = new HashSet<>();
            Set<String> excludedMethodSignatures = new HashSet<>();

            for (CtClass traitClass : ctClass.getInterfaces()) {
                if (traitClass.hasAnnotation(Trait.class)) {

                    String implClassName = getImplClassName(traitClass);
                    CtClass implClass = traitClass.getClassPool().get(implClassName);
                    String implFieldName = implClassName.replace('.', '_');

                    ctClass.addField(CtField.make("private final " + implClassName + " " + implFieldName + " = new " + implClassName + "();", ctClass));

                    if (implClass.subtypeOf(aroundAspectClass)) {
                        for (CtMethod method : aroundAspectClass.getDeclaredMethods()) {
                            excludedMethodSignatures.add(method.getSignature());
                        }

                        for (CtMethod method : ctClass.getDeclaredMethods()) {
                            if ((method.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0 && !addedMethodSignatures.contains(method.getSignature())) {
                                String origMethodName = "_" + method.getName() + "_" + traitClass.getName().replace('.', '_');

                                CtMethod newMethod = CtNewMethod.copy(method, origMethodName, ctClass, null);
                                newMethod.setModifiers(Modifier.FINAL);
                                for (Object attr : method.getMethodInfo().getAttributes()) {
                                    if (attr instanceof AnnotationsAttribute) {
                                        newMethod.getMethodInfo().addAttribute((AnnotationsAttribute) attr);
                                    }
                                }
                                ctClass.addMethod(newMethod);

                                StringBuilder sb = new StringBuilder("{");
                                sb.append("java.lang.reflect.Method m = getClass().getDeclaredMethod(\"" + origMethodName + "\", $sig);");
                                sb.append("m.setAccessible(true);");
                                if (method.getReturnType().getName().equals("void")) {
                                    sb.append(implFieldName + ".around(new com.tsaplin.autojava.trait.CallableMethodImpl(\"" + method.getName() + "\", m), this, $args);");
                                } else {
                                    sb.append("return ($r)" + implFieldName + ".around(new com.tsaplin.autojava.trait.CallableMethodImpl(\"" + method.getName() + "\", m), this, $args);");
                                }
                                sb.append("}");

                                method.setBody(sb.toString());
                                method.addCatch("{ throw $e.getTargetException(); }", ClassPool.getDefault().get("java.lang.reflect.InvocationTargetException"));
                            }
                        }
                    }

                    Set<String> delegatedMethods = new HashSet<>();
                    for (CtMethod method : traitClass.getDeclaredMethods()) {
                        if (!excludedMethodSignatures.contains(method.getSignature())) {
                            delegatedMethods.add(method.getSignature());
                        }
                    }

                    for (CtMethod method : implClass.getDeclaredMethods()) {
                        if (delegatedMethods.contains(method.getSignature())) {
                            CtMethod newMethod = CtNewMethod.copy(method, ctClass, null);
                            if (method.getReturnType().getName().equals("void")) {
                                newMethod.setBody(implFieldName + "." + method.getName() + "($$);");
                            } else {
                                newMethod.setBody("return " + implFieldName + "." + method.getName() + "($$);");
                            }
                            ctClass.addMethod(newMethod);
                            addedMethodSignatures.add(method.getSignature());
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new JavassistBuildException(e);
        }
    }

    private String getImplClassName(CtClass ctClass) {
        try {
            java.lang.annotation.Annotation ann = (java.lang.annotation.Annotation) ctClass.getAnnotation(Trait.class);
            Matcher matcher = TRAIT_CLASS_PATTERN.matcher(ann.toString());
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (ClassNotFoundException e) {}
        throw new IllegalStateException("Implementation class for trait " + ctClass + " cannot be found");
    }
}
