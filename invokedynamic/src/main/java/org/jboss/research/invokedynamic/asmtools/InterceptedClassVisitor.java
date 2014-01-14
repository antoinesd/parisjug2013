package org.jboss.research.invokedynamic.asmtools;

import org.jboss.research.invokedynamic.Interceptable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * This classVisitor is used to detect if the visited class is annoted with {@link org.jboss.research.invokedynamic
 * .Interceptable}
 *
 *
 */
public class InterceptedClassVisitor extends ClassVisitor {
    private static final String INTERCEPTED_NAME = 'L' + Interceptable.class.getName().replace('.', '/') + ';';

    boolean intercepted;

    public InterceptedClassVisitor() {
        super(Opcodes.ASM5);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String annotationName, boolean visible) {
        if (annotationName.equals(INTERCEPTED_NAME)) {
            intercepted = true;
        }
        return null;
    }

    public static boolean isIntercepted(ClassReader classReader) {
        InterceptedClassVisitor cv = new InterceptedClassVisitor();
        classReader.accept(cv, ClassReader.SKIP_CODE);
        return cv.intercepted;
    }
}
