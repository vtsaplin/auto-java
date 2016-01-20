package com.tsaplin.autojava.boundproperty;

import de.icongmbh.oss.maven.plugin.javassist.ClassTransformer;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;

/**
 * Converts regular bean properties to bound properties.
 */
public class BoundPropertyTransformer extends ClassTransformer implements IClassTransformer {

    private static final String PROPERTY_CHANGE_SUPPORT =
            "private final java.beans.PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);";

    private static final String ADD_PROPERTY_CHANGE_LISTENER =
            "public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {\n" +
                    "         this.propertyChangeSupport.addPropertyChangeListener(listener);\n" +
                    "     }";

    private static final String REMOVE_PROPERTY_CHANGE_LISTENER =
            "public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {\n" +
                    "         this.propertyChangeSupport.removePropertyChangeListener(listener);\n" +
                    "     }";

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
        try {
            return ctClass.subtypeOf(ctClass.getClassPool().get(HasBoundProperties.class.getName()));
        } catch (NotFoundException e) {
        }
        return false;
    }

    public void transform(CtClass ctClass) throws JavassistBuildException {
        try {
            ctClass.addField(CtField.make(PROPERTY_CHANGE_SUPPORT, ctClass));
            ctClass.addMethod(CtMethod.make(ADD_PROPERTY_CHANGE_LISTENER, ctClass));
            ctClass.addMethod(CtMethod.make(REMOVE_PROPERTY_CHANGE_LISTENER, ctClass));

            for (CtField field : ctClass.getDeclaredFields()) {

                String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                try {
                    CtMethod setter = ctClass.getDeclaredMethod(setterName);
                    if (setter.hasAnnotation(PropertySetter.class)) {

                        setter.addLocalVariable("oldValue", field.getType());
                        setter.insertBefore("oldValue = this." + field.getName() + ";");
                        setter.insertAfter("this.propertyChangeSupport.firePropertyChange(\"" + field.getName() + "\", oldValue, this." + field.getName() + ");");
                    }
                } catch (NotFoundException e) {
                }
            }
        } catch (Exception e) {
            throw new JavassistBuildException(e);
        }
    }
}
